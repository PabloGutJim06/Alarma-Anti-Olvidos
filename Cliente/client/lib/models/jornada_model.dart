// lib/models/jornada_model.dart
// Este código es una composición basada en patrones oficiales de Flutter/Dart
// y los recursos de referencia indicados (dart.dev/guides, docs.flutter.dev)

class JornadaModel {
  final int id;
  final String fecha;
  final String type;

  // Horas previstas (el plan del día)
  final String horaInicio;
  final String? horaAlmuerzo;
  final String? horaVuelta;
  final String horaFin;

  // Fichajes reales — null significa "aún no fichado"
  // El servidor (JornadaResponseDTO) los devuelve null si no se han registrado
  final String? realInicio;
  final String? realAlmuerzoInicio;
  final String? realAlmuerzoFin;
  final String? realFin;

  const JornadaModel({
    required this.id,
    required this.fecha,
    required this.type,
    required this.horaInicio,
    this.horaAlmuerzo,
    this.horaVuelta,
    required this.horaFin,
    this.realInicio,
    this.realAlmuerzoInicio,
    this.realAlmuerzoFin,
    this.realFin,
  });

  factory JornadaModel.fromJson(Map<String, dynamic> json) {
    return JornadaModel(
      id: json['id'] as int,
      // El servidor manda "dia_semana", no "fecha"
      fecha: json['dia_semana'] as String,
      type: json['type'] as String,
      // El servidor manda "hora_inicio", no "horaInicio"
      horaInicio: json['hora_inicio'] as String,
      horaAlmuerzo: json['horaAlmuerzo'] as String?,
      horaVuelta: json['horaVuelta'] as String?,
      // El servidor manda "hora_fin", no "horaFin"
      horaFin: json['hora_fin'] as String,
      realInicio: json['realInicio'] as String?,
      realAlmuerzoInicio: json['realAlmuerzoInicio'] as String?,
      realAlmuerzoFin: json['realAlmuerzoFin'] as String?,
      realFin: json['realFin'] as String?,
    );
  }
}