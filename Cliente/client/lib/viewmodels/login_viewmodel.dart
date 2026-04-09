//Aquí estará la lógica de la aplicación
// Capturará los datos de la vista, llamará al service y manejara los estados

// lib/viewmodels/login_viewmodel.dart
import 'package:flutter/material.dart';

import '../services/auth_service.dart';
import '../models/user_model.dart';

class LoginViewModel extends ChangeNotifier {
  // Ahora Dart sí sabe qué es AuthService porque lo hemos importado arriba
  final AuthService _authService = AuthService();

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  UserModel? _currentUser;
  UserModel? get currentUser => _currentUser;

  Future<bool> login(String username, String password) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final user = await _authService.login(username, password);

      if (user != null) {
        _currentUser = user as UserModel?;
        return true;
      } else {
        _errorMessage = 'Credenciales incorrectas. ¿Eres un espía de la Marina?';
        return false;
      }
    } catch (e) {
      print('🔥 ERROR REAL DEL KRAKEN: $e');

      // Y lo mostramos en la pantalla para que lo veas
      _errorMessage = 'Error exacto: $e';
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}
