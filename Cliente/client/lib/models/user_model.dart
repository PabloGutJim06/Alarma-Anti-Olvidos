// lib/models/user_model.dart

class Jornada {
  final int id;
  final String fecha;
  final String type;
  final String hora_inicio;
  final String hora_fin;

  Jornada({
    required this.id,
    required this.fecha,
    required this.type,
    required this.hora_inicio,
    required this.hora_fin
  });

  // Este es el truco de magia que convierte el mapa JSON en nuestro objeto
  factory Jornada.fromJson(Map<String, dynamic> json) {
    return Jornada(
      id: json['id'] ?? 0, // ¡Corregido! Ahora si no hay ID, pone un 0 en vez de un texto
      fecha: json['fecha'] ?? 'Fecha desconocida',
      type: json['tipo'] ?? 'Sin tipo',

      // OJO AQUÍ: Comprobamos ambas posibles claves por si el backend usa espacios o barras bajas
      hora_inicio: json['hora_inicio'] ?? json['hora inicio'] ?? 'Sin hora de inicio',
      hora_fin: json['hora_fin'] ?? json['hora fin'] ?? 'Sin hora de fin',
    );
  }

  // Como tu HomeScreen busca "jornada.estado", hacemos que devuelva el "type"
  String get estado => type;
}

class UserModel {
  final int id;
  final String username;
  final bool type; // type en el UserModel (true/false)
  final List<Jornada> jornadas;

  UserModel({
    required this.id,
    required this.username,
    required this.type,
    required this.jornadas,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    // Extraemos la lista de jornadas de forma segura
    var jornadasList = json['jornadas'] as List? ?? [];
    List<Jornada> jornadasMapeadas = jornadasList.map((j) => Jornada.fromJson(j)).toList();

    return UserModel(
      id: json['id'] ?? 0, // ¡Corregido! Int
      username: json['username'] ?? 'Tripulante anónimo',
      type: json['tipo'] ?? false, // ¡Corregido! Bool
      jornadas: jornadasMapeadas,
    );
  }
}