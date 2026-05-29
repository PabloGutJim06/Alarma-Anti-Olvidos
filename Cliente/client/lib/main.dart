// lib/main.dart
import 'package:client/firebase_options.dart';
import 'package:client/viewmodels/fichaje_viewmodel.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:io';
import 'views/screens/login_screen.dart';
import 'views/screens/main_navigation_screen.dart';
import 'viewmodels/login_viewmodel.dart';
import 'services/notification_service.dart';
import 'package:window_manager/window_manager.dart';
import 'services/tray_service.dart';

final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await windowManager.ensureInitialized();

  List<String> args = Platform.executableArguments;
  bool startMinimized = args.contains("--minimized");

  WindowOptions windowOptions = const WindowOptions(
    size: Size(800, 600),
    center: true,
    title: "ESAIL IT Alarma",
  );

  await windowManager.setPreventClose(true);

  await windowManager.waitUntilReadyToShow(windowOptions, () async {
    if (startMinimized) {
      await windowManager.hide();
    } else {
      await windowManager.show();
      await windowManager.focus();
    }
  });

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  final loginVM = LoginViewModel();
  bool isLoggedIn = await loginVM.checkAutoLogin();

  if (isLoggedIn && loginVM.currentUser != null) {
    NotificationService.conectarWebSocket(
      loginVM.currentUser!.username,
      navigatorKey,
    );
  }

  await NotificationService.iniciarEscuchador(navigatorKey);
  await TrayService.instance.inicializarVigia();

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: loginVM),
        ChangeNotifierProvider(create: (_) => FichajeViewModel()),
      ],
      child: EsailItApp(isLoggedIn: isLoggedIn),
    ),
  );
}

class EsailItApp extends StatelessWidget {
  final bool isLoggedIn;
  const EsailItApp({super.key, required this.isLoggedIn});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: navigatorKey,
      title: 'ESAIL IT Alarma',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: const Color(0xFFF9F6F0),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFFB8860B),
          primary: const Color(0xFFB8860B),
          onPrimary: Colors.white,
        ),
        inputDecorationTheme: const InputDecorationTheme(
          labelStyle: TextStyle(color: Colors.black),
          enabledBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Color(0xFFB8860B)),
          ),
          focusedBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Color(0xFFB8860B), width: 2),
          ),
        ),
      ),
      home: isLoggedIn ? const MainNavigationScreen() : const LoginScreen(),
    );
  }
}