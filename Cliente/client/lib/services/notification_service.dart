import 'dart:async';
import 'dart:developer' as developer;
import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:local_notifier/local_notifier.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:window_manager/window_manager.dart';
// Añade este import arriba del todo
import '../config/app_config.dart';

class NotificationService {
  static StompClient? _stompClient;

  // --- PUNTO DE ENTRADA PRINCIPAL ---
  static Future<void> iniciarEscuchador(GlobalKey<NavigatorState> navigatorKey) async {
    if (Platform.isWindows) {
      // En Windows, primero preparamos las notificaciones nativas
      await localNotifier.setup(
        appName: 'ESAIL IT Alarma',
        shortcutPolicy: ShortcutPolicy.requireCreate,
      );

      // Intentamos conectar el WebSocket si ya hay un usuario guardado
      const storage = FlutterSecureStorage();
      String? username = await storage.read(key: 'username');
      if (username != null) {
        conectarWebSocket(username, navigatorKey);
      }
    } else {
      // En móvil seguimos usando Firebase
      _iniciarVigiaFirebase(navigatorKey);
    }
  }

  // --- LÓGICA DE WEBSOCKET (WINDOWS) ---
  static void conectarWebSocket(String username, GlobalKey<NavigatorState> navigatorKey) {

    // ← Protección: si ya hay cliente activo, no creamos otro
    if (_stompClient != null && _stompClient!.connected) {
      developer.log('⚠️ WebSocket ya conectado, ignorando nueva conexión', name: 'VIGIA_WS');
      return;
    }

    // Desconectamos el cliente anterior si existe pero no está conectado
    _stompClient?.deactivate();

    final String urlLimpia = AppConfig.wsUrl;

    // Guardamos referencia local para usar dentro del onConnect de forma segura
    StompClient? clienteLocal;

    clienteLocal = StompClient(
      config: StompConfig(
        url: urlLimpia,
        onConnect: (frame) {
          developer.log('✅ ESTABLECIDO: Conectado al servidor para el usuario: $username', name: 'VIGIA_WS');

          // ← Usamos clienteLocal en lugar de _stompClient!
          clienteLocal!.subscribe(
            destination: '/topic/notificaciones/$username',
            callback: (frame) {
              developer.log('📩 LLEGÓ UN MENSAJE DEL SERVIDOR!', name: 'VIGIA_WS');
              print('📨 WebSocket recibido RAW: ${frame.body}');

              if (frame.body != null) {
                final Map<String, dynamic> data = jsonDecode(frame.body!);

                print('📨 title: ${data['title']}');
                print('📨 body: ${data['body']}');

                if (Platform.isWindows) {
                  windowManager.show();
                  windowManager.focus();
                }

                _mostrarAlarmaVisual(
                  navigatorKey,
                  data['title'] ?? 'Alarma de Fichaje',
                  data['body'] ?? 'Tienes una nueva notificación.',
                );

                if (Platform.isWindows) {
                  _mostrarNotificacionNativaWindows(
                    data['title'] ?? 'Alarma',
                    data['body'] ?? 'Revisa el sistema',
                  );
                }
              }
            },
          );
        },
        webSocketConnectHeaders: {
          'Connection': 'upgrade',
          'Upgrade': 'websocket',
        },
        onWebSocketError: (error) => developer.log('🔥 ERROR DE RED (WS): $error', name: 'VIGIA_WS'),
        onStompError: (frame) => developer.log('🚫 ERROR PROTOCOLO STOMP: ${frame.body}', name: 'VIGIA_WS'),
        onDisconnect: (frame) => developer.log('🔌 DESCONECTADO DEL SERVIDOR', name: 'VIGIA_WS'),
      ),
    );

    _stompClient = clienteLocal;
    _stompClient!.activate();
  }

  // Asegúrate de que este método cree y muestre la notificación nativa
  static void _mostrarNotificacionNativaWindows(String title, String body) {
    LocalNotification notification = LocalNotification(
      title: title,
      body: body,
      silent: false, // Para que suene el "pling" de Windows
    );

    notification.show(); // Esto lanza el banner abajo a la derecha
  }

  static void _onConnect(StompFrame frame, String username, GlobalKey<NavigatorState> navigatorKey) {
    print("✅ ¡Conectado al WebSocket como $username!");

    _stompClient?.subscribe(
      destination: '/topic/notificaciones/$username',
      callback: (StompFrame frame) {
        if (frame.body != null) {
          final Map<String, dynamic> data = jsonDecode(frame.body!);
          String titulo = data['title'] ?? "Aviso";
          String mensaje = data['body'] ?? "Tienes una nueva notificación";

          // Disparamos ambos: el de Windows y el cartel interno
          _lanzarNotificacionWindows(titulo, mensaje);
          _mostrarAlarmaVisual(navigatorKey, titulo, mensaje);
        }
      },
    );
  }

  // --- NOTIFICACIONES NATIVAS Y VISUALES ---
  static void _lanzarNotificacionWindows(String titulo, String cuerpo) {
    LocalNotification notification = LocalNotification(
      title: titulo,
      body: cuerpo,
    );
    notification.show();
  }

  static bool _dialogoVisible = false;

  static void _mostrarAlarmaVisual(
      GlobalKey<NavigatorState> navigatorKey, String title, String body) {

    if (_dialogoVisible) return;

    // Esperamos a que windowManager.show() complete y luego mostramos el diálogo
    Future.delayed(const Duration(milliseconds: 300), () {
      final context = navigatorKey.currentContext;
      if (context == null) return;
      if (_dialogoVisible) return;

      _dialogoVisible = true;
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
          backgroundColor: const Color(0xFFF9F6F0),
          title: Row(
            children: [
              const Icon(Icons.warning_amber_rounded, color: Color(0xFFB8860B), size: 30),
              const SizedBox(width: 10),
              Expanded(
                child: Text(title,
                    style: const TextStyle(fontWeight: FontWeight.bold)),
              ),
            ],
          ),
          content: Text(body),
          actions: [
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFB8860B)),
              onPressed: () {
                _dialogoVisible = false;
                Navigator.of(context).pop();
              },
              child: const Text('¡Ir a Fichar!',
                  style: TextStyle(color: Colors.white)),
            ),
          ],
        ),
      ).then((_) => _dialogoVisible = false);
    });
  }

  // --- FIREBASE (MÓVIL) ---
  static void _iniciarVigiaFirebase(GlobalKey<NavigatorState> navigatorKey) {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      if (message.notification != null) {
        _mostrarAlarmaVisual(
            navigatorKey,
            message.notification!.title ?? 'Alarma',
            message.notification!.body ?? 'Aviso'
        );
      }
    });
  }
}