import 'dart:io';
import 'package:flutter/material.dart';
import 'package:tray_manager/tray_manager.dart';
import 'package:window_manager/window_manager.dart';

class TrayService with TrayListener {
  // ¡Instancia única para no tener mil vigías gritando a la vez!
  static final TrayService instance = TrayService._();
  TrayService._();

  Future<void> inicializarVigia() async {
    // 1. Registramos este servicio como escuchador
    trayManager.addListener(this);

    // 2. Configuramos el icono (asegúrate de que el asset exista)
    // En Windows, se recomienda un .ico o un .png de 32x32
    await trayManager.setIcon(
      Platform.isWindows ? 'assets/app_icon.ico' : 'assets/app_icon.png',
    );

    // 3. Creamos un menú rápido (opcional pero profesional)
    Menu menu = Menu(
      items: [
        MenuItem(key: 'show_window', label: 'Abrir ESAIL IT'),
        MenuItem.separator(),
        MenuItem(key: 'exit_app', label: 'Salir completamente'),
      ],
    );
    await trayManager.setContextMenu(menu);
  }

  // --- ESCUCHADORES DE EVENTOS ---

  @override
  void onTrayIconMouseDown() {
    // ¡Al hacer clic simple, mostramos el barco!
    _mostrarVentana();
  }

  @override
  void onTrayIconRightMouseDown() {
    // Al hacer clic derecho, el tray_manager mostrará el menú automáticamente
    trayManager.popUpContextMenu();
  }

  @override
  void onTrayMenuItemClick(MenuItem menuItem) async {
    if (menuItem.key == 'show_window') {
      _mostrarVentana();
    } else if (menuItem.key == 'exit_app') {
      // ¡Orden de retirada total!
      // Aquí sí cerramos el proceso de verdad.
      await windowManager.destroy();
    }
  }

  void _mostrarVentana() async {
    await windowManager.show();
    await windowManager.focus();
  }
}