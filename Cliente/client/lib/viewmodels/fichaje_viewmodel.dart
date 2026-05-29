// lib/viewmodels/fichaje_viewmodel.dart
// Este código es una composición basada en patrones oficiales de Flutter/Dart
// y los recursos de referencia indicados

import 'package:flutter/foundation.dart';
import '../models/jornada_model.dart';
import '../repositories/jornada_repository.dart';

// Estados posibles de la pantalla de fichaje
enum FichajeStatus { inicial, cargando, exito, error }

class FichajeViewModel extends ChangeNotifier {
  final JornadaRepository _repository;

  FichajeViewModel({JornadaRepository? repository})
      : _repository = repository ?? JornadaRepository();

  FichajeStatus _status = FichajeStatus.inicial;
  JornadaModel? _jornadaHoy;
  String? _errorMessage;
  // Para saber qué botón específico está cargando
  String? _tipoFichajeCargando;

  // Getters — la UI solo lee, nunca escribe directamente
  FichajeStatus get status => _status;
  JornadaModel? get jornadaHoy => _jornadaHoy;
  String? get errorMessage => _errorMessage;
  String? get tipoFichajeCargando => _tipoFichajeCargando;

  // Carga la jornada de hoy para este usuario
  Future<void> cargarJornadaDeHoy(int usuarioId, String token) async {
    _status = FichajeStatus.cargando;
    _errorMessage = null;
    notifyListeners();

    try {
      final jornadas = await _repository.obtenerJornadasDeUsuario(
        usuarioId,
        token,
      );

      // Buscamos la jornada de hoy comparando la fecha
      final hoy = DateTime.now();
      final String hoyStr =
          '${hoy.year}-${hoy.month.toString().padLeft(2, '0')}-${hoy.day.toString().padLeft(2, '0')}';

      final jornadasDeHoy = jornadas
          .where((j) => j.fecha == hoyStr)
          .toList();

      if (jornadasDeHoy.isEmpty) {
        _jornadaHoy = null;
        _status = FichajeStatus.exito; // No es error, simplemente no hay jornada hoy
        return;
      }

      jornadasDeHoy.sort((a, b) => b.id.compareTo(a.id));
      _jornadaHoy = jornadasDeHoy.first;
      _status = FichajeStatus.exito;
    } catch (e) {
      _status = FichajeStatus.error;
      _errorMessage = e.toString();
    }

    notifyListeners();
  }

  Future<void> recargarSilencioso(int usuarioId, String token) async {
    // NO cambiamos _status a cargando — la UI no se reconstruye con spinner
    try {
      final jornadas = await _repository.obtenerJornadasDeUsuario(
        usuarioId,
        token,
      );

      final hoy = DateTime.now();
      final String hoyStr =
          '${hoy.year}-${hoy.month.toString().padLeft(2, '0')}-${hoy.day.toString().padLeft(2, '0')}';

      final jornadasDeHoy = jornadas
          .where((j) => j.fecha == hoyStr)
          .toList();

      if (jornadasDeHoy.isEmpty) return; // Sin jornada hoy, no tocamos nada

      jornadasDeHoy.sort((a, b) => b.id.compareTo(a.id));
      _jornadaHoy = jornadasDeHoy.first;

      // Solo notificamos al final — un único rebuild, sin spinner intermedio
      notifyListeners();
    } catch (_) {
      // Recarga silenciosa — si falla, no mostramos error, simplemente no actualizamos
    }
  }

  // Registra un fichaje y actualiza el estado local
  Future<void> fichar(int usuarioId, String tipo, String token) async {
    if (_jornadaHoy == null) return;

    _tipoFichajeCargando = tipo;
    notifyListeners();

    try {
      final jornadaActualizada = await _repository.registrarFichaje(
        usuarioId,
        _jornadaHoy!.id,
        tipo,
        token,
      );
      // Actualizamos la jornada con los datos frescos del servidor
      _jornadaHoy = jornadaActualizada;
      _errorMessage = null;
    } on FichajeYaRegistradoException catch (e) {
      _errorMessage = e.toString();
    } on FichajeInvalidoException catch (e) {
      _errorMessage = e.toString();
    } catch (e) {
      _errorMessage = 'Error de conexión. Inténtalo de nuevo.';
    } finally {
      _tipoFichajeCargando = null;
      notifyListeners();
    }
  }
  void limpiarError() {
    _errorMessage = null;
    // No llamamos notifyListeners() — no necesitamos rebuild por esto
  }
}