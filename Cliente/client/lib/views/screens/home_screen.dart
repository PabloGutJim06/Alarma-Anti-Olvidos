// En lib/views/screens/home_screen.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/login_viewmodel.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  static const Color goldenBrown = Color(0xFFB8860B);

  @override
  Widget build(BuildContext context) {
    // 🔭 Leemos al usuario desde el ViewModel
    final viewModel = context.watch<LoginViewModel>();
    final user = viewModel.currentUser;

    // Si por algún motivo llegamos aquí y el usuario es null, mostramos un error
    if (user == null) {
      return const Scaffold(
        body: Center(child: Text('Error: No se encontraron datos del tripulante.')),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: Text('Perfil de ${user.username}'),
        backgroundColor: goldenBrown,
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Cabecera del usuario
            Row(
              children: [
                const CircleAvatar(
                  radius: 40,
                  backgroundColor: goldenBrown,
                  child: Icon(Icons.person, size: 40, color: Colors.white),
                ),
                const SizedBox(width: 20),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '¡Bienvenido a bordo!',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    Text(
                      user.username,
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: goldenBrown,
                      ),
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 30),

            // Título de la lista
            const Text(
              'Tus Jornadas',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const Divider(color: goldenBrown),

            // 📜 Lista de Jornadas
            Expanded(
              child: user.jornadas.isEmpty
                  ? const Center(child: Text('Aún no tienes jornadas registradas.'))
                  : ListView.builder(
                itemCount: user.jornadas.length,
                itemBuilder: (context, index) {
                  final jornada = user.jornadas[index];
                  return Card(
                    margin: const EdgeInsets.symmetric(vertical: 8.0),
                    child: ListTile(
                      leading: const Icon(Icons.access_time, color: goldenBrown),
                      title: Text('Fecha: ${jornada.fecha}'),
                      subtitle: Text('Estado: ${jornada.estado}'),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}