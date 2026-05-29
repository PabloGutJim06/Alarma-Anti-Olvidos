import 'dart:io';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import '../models/user_model.dart';
import '../config/app_config.dart';

class AuthService {
  // 1. Configuración inicial
  final String _baseUrl = AppConfig.baseUrl;
  final _storage = const FlutterSecureStorage(); // Nuestra caja fuerte para el JWT

  // --- MÉTODOS DE SESIÓN ---

  // Obtener el token guardado
  Future<String?> getToken() async {
    return await _storage.read(key: 'jwt_token');
  }

  // Cerrar sesión (limpiar caja fuerte)
  Future<void> logout() async {
    await _storage.delete(key: 'jwt_token');
    await _storage.delete(key: 'username');
  }

  // --- LÓGICA DE LOGIN PRINCIPAL ---

  Future<UserModel?> login(String username, String password) async {
    try {
      // 💥 DISPARO 1: Login y obtención del JWT
      final loginResponse = await http.post(
        Uri.parse('$_baseUrl/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'username': username, 'password': password}),
      );

      if (loginResponse.statusCode == 200) {
        final Map<String, dynamic> loginData = jsonDecode(loginResponse.body);

        // Extraemos el Token que genera tu JwtService en el servidor
        final String? jwt = loginData['token'];

        if (jwt != null) {
          // 🛡️ Guardamos el JWT y el username para el auto-login
          await _storage.write(key: 'jwt_token', value: jwt);
          await _storage.write(key: 'username', value: username);

          // 💥 DISPARO 2: Obtener perfil completo usando el JWT
          final perfilResponse = await http.get(
            Uri.parse('$_baseUrl/me/$username'),
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer $jwt', // Mandamos el sello de seguridad
            },
          );

          if (perfilResponse.statusCode == 200) {
            final Map<String, dynamic> userData = jsonDecode(perfilResponse.body);
            UserModel user = UserModel.fromJson(userData);

            // 💥 DISPARO 3: Actualizar el Token de Firebase (Notificaciones)
            // Lo hacemos de forma asíncrona para no bloquear al usuario
            _updateFirebaseToken(user.id, jwt);

            return user;
          } else {
            throw Exception('Error al obtener perfil: ${perfilResponse.statusCode}');
          }
        } else {
          // El servidor no devolvió un token (quizás login incorrecto)
          return null;
        }
      } else if (loginResponse.statusCode == 401) {
        return null; // Credenciales incorrectas
      } else {
        throw Exception('Error del servidor: ${loginResponse.statusCode}');
      }
    } catch (e) {
      print('🔥 ERROR EN AUTH_SERVICE: $e');
      rethrow;
    }
  }

  Future<UserModel?> verificarSesion(String token) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/verificar-sesion'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token', // Enviamos el JWT
        },
      );

      if (response.statusCode == 200) {
        // Si el servidor dice OK, parseamos el JSON del usuario
        return UserModel.fromJson(jsonDecode(response.body));
      } else {
        // Si es 401 o cualquier otro, la sesión no es válida
        return null;
      }
    } catch (e) {
      print('🔥 ERROR AL VERIFICAR SESIÓN: $e');
      return null;
    }
  }

  // --- LÓGICA DE FIREBASE (Notificaciones) ---

  Future<void> _updateFirebaseToken(int userId, String jwt) async {
    try {
      if (Platform.isWindows) {
        print("ℹ️ Windows no usa Firebase Messaging. Saltando token.");
        return;
      }
      String? deviceToken = await FirebaseMessaging.instance.getToken();
      if (deviceToken != null) {
        final response = await http.patch(
          Uri.parse('$_baseUrl/$userId/token'),
          headers: {
            'Content-Type': 'text/plain',
            'Authorization': 'Bearer $jwt', // También protegemos este envío
          },
          body: deviceToken,
        );

        if (response.statusCode == 200) {
          print("✅ Token de Firebase actualizado en BD.");
        }
      }
    } catch (e) {
      print("⚠️ Error actualizando token de Firebase: $e");
    }
  }
}