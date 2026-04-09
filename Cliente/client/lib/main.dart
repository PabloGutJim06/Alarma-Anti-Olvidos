import 'package:flutter/material.dart';
import 'package:provider/provider.dart'; // ¡La munición clave que nos faltaba!
import 'views/screens/login_screen.dart';
import 'viewmodels/login_viewmodel.dart'; // Importamos al estratega

void main() {
  runApp(const EsailItApp());
}

class EsailItApp extends StatelessWidget {
  const EsailItApp({super.key});

  @override
  Widget build(BuildContext context) {
    // 🛡️ ¡EL ESCUDO MÁGICO! Envolvemos la app en un MultiProvider
    return MultiProvider(
      providers: [
        // Aquí nacen los ViewModels. Sobrevivirán mientras la app esté viva.
        ChangeNotifierProvider(create: (_) => LoginViewModel()),
      ],
      child: MaterialApp(
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
              borderSide: BorderSide(color: Color(0xFFB8860B), width: 2.0),
            ),
          ),
        ),
        home: const LoginScreen(),
      ),
    );
  }
}