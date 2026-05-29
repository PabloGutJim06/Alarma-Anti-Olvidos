// lib/viewmodels/login_viewmodel.dart
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../services/auth_service.dart';
import '../models/user_model.dart';
import '../services/notification_service.dart';

class LoginViewModel extends ChangeNotifier {
  final AuthService _authService = AuthService();
  final _storage = const FlutterSecureStorage();

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  UserModel? _currentUser;
  UserModel? get currentUser => _currentUser;

  Future<String?> getToken() => _authService.getToken();

  Future<bool> checkAutoLogin() async {
    _isLoading = true;
    _errorMessage = null;

    try {
      String? token = await _authService.getToken();

      if (token != null) {
        final user = await _authService.verificarSesion(token);

        if (user != null) {
          _currentUser = user;
          _isLoading = false;
          notifyListeners();
          return true;
        } else {
          // El servidor respondió explícitamente con 401 → token inválido → borramos
          await _authService.logout();
          return false;
        }
      }
    } catch (e) {
      // Error de conexión — servidor apagado o sin red
      // NO borramos el token — puede ser válido, solo no hay conexión ahora
      print('⚠Auto-login fallido (servidor no disponible): $e');
    }

    _isLoading = false;
    notifyListeners();
    return false;
  }
  Future<bool> login(String username, String password,
      GlobalKey<NavigatorState> navigatorKey) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final user = await _authService.login(username, password);

      if (user != null) {
        _currentUser = user;
        _errorMessage = null;

        // Reconectamos el WebSocket
        // si la app arrancó sin servidor
        NotificationService.conectarWebSocket(user.username, navigatorKey);

        return true;
      } else {
        _errorMessage = 'Usuario o contraseña incorrectos.';
        return false;
      }
    } catch (e) {
      print('ERROR EN LOGIN_VIEWMODEL: $e');
      _errorMessage = 'No se pudo conectar con el servidor.';
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> logout() async {
    _isLoading = true;
    notifyListeners();

    try {
      await _authService.logout();
      _currentUser = null;
      _errorMessage = null;
    } catch (e) {
      print('Error al cerrar sesión: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}