import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/user_model.dart';

class AuthService {
  // Ajusta esto a tu ruta base real en Spring Boot
  final String _baseUrl = 'http://localhost:8080/api/usuarios';

  Future<UserModel?> login(String username, String password) async {
    try {
      // 💥 DISPARO 1: Hablar con el guardia (Validar contraseña)
      final loginResponse = await http.post(
        Uri.parse('$_baseUrl/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'username': username, 'password': password}),
      );

      if (loginResponse.statusCode == 200) {
        // Comprobamos si el guardia nos deja pasar (puede llegar como bool o string)
        final dynamic isValido = jsonDecode(loginResponse.body);

        if (isValido == true || isValido == 'true') {
          // 💥 DISPARO 2: ¡El guardia dijo sí! Ahora vamos a la bóveda a por los datos
          final perfilResponse = await http.get(
            // Fíjate que esto es un GET y le pasamos el username en la URL
            Uri.parse('$_baseUrl/me/$username'),
            headers: {'Content-Type': 'application/json'},
          );

          if (perfilResponse.statusCode == 200) {
            // ¡Botín asegurado! Lo convertimos a nuestro objeto Dart
            final Map<String, dynamic> data = jsonDecode(perfilResponse.body);
            return UserModel.fromJson(data);
          } else {
            throw Exception('El guardia me dejó pasar, pero la bóveda estaba cerrada.');
          }
        } else {
          // El guardia dijo false (contraseña incorrecta)
          return null;
        }
      } else {
        throw Exception('El guardia me ignoró. Error del servidor: ${loginResponse.statusCode}');
      }
    } catch (e) {
      // Capturamos el error real y lo mostramos en consola
      print('🔥 ERROR DEL KRAKEN (AuthService): $e');
      throw Exception('Error de conexión en el doble disparo: $e');
    }
  }
}