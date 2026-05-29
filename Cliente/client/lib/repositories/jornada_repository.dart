// lib/repositories/jornada_repository.dart
// Este código es una composición basada en patrones oficiales de Flutter/Dart
// y los recursos de referencia indicados

import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/jornada_model.dart';
import '../config/app_config.dart';

class JornadaRepository {
  final String _baseUrl = AppConfig.baseUrl;

  Future<List<JornadaModel>> obtenerJornadasDeUsuario(
      int usuarioId,
      String token,
      ) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/$usuarioId/jornadas'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList
          .map((json) => JornadaModel.fromJson(json as Map<String, dynamic>))
          .toList();
    } else {
      throw Exception('Error al obtener jornadas: ${response.statusCode}');
    }
  }

  Future<JornadaModel> registrarFichaje(
      int usuarioId,
      int jornadaId,
      String tipo,
      String token,
      ) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/$usuarioId/jornadas/$jornadaId/fichar?tipo=$tipo'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      return JornadaModel.fromJson(
        jsonDecode(response.body) as Map<String, dynamic>,
      );
    } else if (response.statusCode == 409) {
      // Leemos el mensaje real del servidor
      final body = jsonDecode(response.body) as Map<String, dynamic>;
      final mensaje = body['mensaje'] as String? ??
          'El fichaje "$tipo" ya fue registrado anteriormente.';
      throw FichajeYaRegistradoException(mensaje);
    } else if (response.statusCode == 400) {
      throw FichajeInvalidoException(tipo);
    } else {
      throw Exception('Error del servidor: ${response.statusCode}');
    }
  }
}

class FichajeYaRegistradoException implements Exception {
  final String mensaje;
  FichajeYaRegistradoException(this.mensaje);

  @override
  String toString() => mensaje;
}

class FichajeInvalidoException implements Exception {
  final String tipo;
  FichajeInvalidoException(this.tipo);

  @override
  String toString() => 'El tipo de fichaje "$tipo" no es válido.';
}