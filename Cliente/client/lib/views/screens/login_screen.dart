import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/login_viewmodel.dart';
import 'home_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  // Controladores para atrapar la munición (texto)
  final TextEditingController _userController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  static const Color boneWhite = Color(0xFFF9F6F0);
  static const Color goldenBrown = Color(0xFFB8860B);

  @override
  void dispose() {
    // ¡REGLA DE ORO! Siempre destruye los controladores para evitar memory leaks
    _userController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _attemptLogin() async {
    final viewModel = context.read<LoginViewModel>();

    // Disparamos la petición
    final success = await viewModel.login(
      _userController.text.trim(),
      _passwordController.text,
    );

    // ¡CRÍTICO! Si el usuario cerró la app mientras cargaba, el "context" ya no existe.
    if (!mounted) return;

    if (success) {
      // ¡Impacto directo! Navegamos y destruimos la ruta anterior
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const HomeScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // Escuchamos el ViewModel para saber si hay errores o si está cargando
    final viewModel = context.watch<LoginViewModel>();

    return Scaffold(
      backgroundColor: boneWhite,
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 32.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text(
                  'ESAIL IT',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 42.0,
                    fontWeight: FontWeight.bold,
                    color: goldenBrown,
                    letterSpacing: 2.0,
                  ),
                ),
                const SizedBox(height: 60.0),

                TextField(
                  controller: _userController,
                  decoration: const InputDecoration(
                    labelText: 'Nombre de Usuario',
                    prefixIcon: Icon(Icons.person, color: goldenBrown),
                  ),
                ),
                const SizedBox(height: 20.0),

                TextField(
                  controller: _passwordController,
                  obscureText: true,
                  decoration: const InputDecoration(
                    labelText: 'Contraseña',
                    prefixIcon: Icon(Icons.lock, color: goldenBrown),
                  ),
                ),
                const SizedBox(height: 20.0),

                // Mostrar mensaje de error si existe
                if (viewModel.errorMessage != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 20.0),
                    child: Text(
                      viewModel.errorMessage!,
                      style: const TextStyle(color: Colors.red, fontWeight: FontWeight.bold),
                      textAlign: TextAlign.center,
                    ),
                  ),

                // Botón de Login (O indicador de carga)
                viewModel.isLoading
                    ? const Center(child: CircularProgressIndicator(color: goldenBrown))
                    : ElevatedButton(
                  onPressed: _attemptLogin,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: goldenBrown,
                    padding: const EdgeInsets.symmetric(vertical: 16.0),
                  ),
                  child: const Text(
                    'INGRESAR',
                    style: TextStyle(color: Colors.white, fontSize: 16.0),
                  ),
                ),

                const SizedBox(height: 40.0),
                // ... (Mantén aquí el Row de "¿No tienes cuenta?" del código anterior)
              ],
            ),
          ),
        ),
      ),
    );
  }
}