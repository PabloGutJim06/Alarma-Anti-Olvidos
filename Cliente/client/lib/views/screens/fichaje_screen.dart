// lib/views/screens/fichaje_screen.dart
// Este código es una composición basada en patrones oficiales de Flutter/Dart
// y los recursos de referencia indicados

import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/login_viewmodel.dart';
import '../../viewmodels/fichaje_viewmodel.dart';
import '../../models/jornada_model.dart';

class FichajeScreen extends StatefulWidget {
  const FichajeScreen({super.key});

  @override
  State<FichajeScreen> createState() => _FichajeScreenState();
}

class _FichajeScreenState extends State<FichajeScreen>
    with WidgetsBindingObserver {
  static const Color goldenBrown = Color(0xFFB8860B);

  late Timer _timer;
  DateTime _now = DateTime.now();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _timer = Timer.periodic(const Duration(seconds: 1), (_) {
      setState(() => _now = DateTime.now());
    });
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _cargarJornada();
    });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _timer.cancel();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _recargarSilenciosa();
    }
  }

  Future<void> _recargarSilenciosa() async {
    final loginVM = context.read<LoginViewModel>();
    final fichajeVM = context.read<FichajeViewModel>();
    final user = loginVM.currentUser;
    final token = await loginVM.getToken();

    if (user != null && token != null) {
      await fichajeVM.recargarSilencioso(user.id, token);
    }
  }

  Future<void> _cargarJornada() async {
    final loginVM = context.read<LoginViewModel>();
    final fichajeVM = context.read<FichajeViewModel>();
    final user = loginVM.currentUser;
    final token = await loginVM.getToken();

    if (user != null && token != null) {
      await fichajeVM.cargarJornadaDeHoy(user.id, token);
    }
  }

  // --- LÓGICA DE NEGOCIO — qué botón está activo ahora mismo ---
  String? _tipoFichajeActivo(JornadaModel jornada) {
    final ahora = TimeOfDay.fromDateTime(_now);

    final horaFin      = _parseHora(jornada.horaFin);
    final horaVuelta   = _parseHora(jornada.horaVuelta);
    final horaAlmuerzo = _parseHora(jornada.horaAlmuerzo);
    final horaInicio   = _parseHora(jornada.horaInicio);

    // FIN — solo si no se ha fichado ya
    if (horaFin != null &&
        _despuesDe(ahora, horaFin) &&
        jornada.realFin == null)
      return 'FIN';

    // ALMUERZO_FIN — solo si la jornada no avanzó más allá (realFin ya puesto)
    if (horaVuelta != null &&
        _despuesDe(ahora, horaVuelta) &&
        jornada.realAlmuerzoFin == null &&
        jornada.realFin == null) // ← no se saltó el fin
      return 'ALMUERZO_FIN';

    // ALMUERZO_INICIO — solo si la jornada no avanzó más allá
    if (horaAlmuerzo != null &&
        _despuesDe(ahora, horaAlmuerzo) &&
        jornada.realAlmuerzoInicio == null &&
        jornada.realAlmuerzoFin == null && // ← no se saltó la vuelta
        jornada.realFin == null)           // ← ni el fin
      return 'ALMUERZO_INICIO';

    // INICIO — solo si ningún evento posterior ocurrió
    if (horaInicio != null &&
        _despuesDe(ahora, horaInicio) &&
        jornada.realInicio == null &&
        jornada.realAlmuerzoInicio == null &&
        jornada.realAlmuerzoFin == null &&
        jornada.realFin == null)
      return 'INICIO';

    return null;
  }

  // Parsea "08:00:00" → TimeOfDay
  TimeOfDay? _parseHora(String? hora) {
    if (hora == null) return null;
    final partes = hora.split(':');
    if (partes.length < 2) return null;
    return TimeOfDay(
      hour: int.parse(partes[0]),
      minute: int.parse(partes[1]),
    );
  }

  // true si ahora >= referencia
  bool _despuesDe(TimeOfDay ahora, TimeOfDay referencia) {
    return ahora.hour > referencia.hour ||
        (ahora.hour == referencia.hour && ahora.minute >= referencia.minute);
  }

  String _formatTime(DateTime time) =>
      '${time.hour.toString().padLeft(2, '0')}:'
          '${time.minute.toString().padLeft(2, '0')}:'
          '${time.second.toString().padLeft(2, '0')}';

  String _formatDate(DateTime date) =>
      '${date.day.toString().padLeft(2, '0')}/'
          '${date.month.toString().padLeft(2, '0')}/'
          '${date.year}';

  @override
  Widget build(BuildContext context) {
    final loginVM  = context.watch<LoginViewModel>();
    final fichajeVM = context.watch<FichajeViewModel>();
    final user    = loginVM.currentUser;
    final jornada = fichajeVM.jornadaHoy;

    // tipoActivo se recalcula en cada build gracias al Timer (cada segundo)
    final tipoActivo = jornada != null ? _tipoFichajeActivo(jornada) : null;

    // Mostramos el error una sola vez y lo limpiamos
    if (fichajeVM.errorMessage != null) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(fichajeVM.errorMessage!),
            backgroundColor: Colors.red[700],
          ),
        );
        fichajeVM.limpiarError();
      });
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'ESAIL IT',
          style: TextStyle(fontWeight: FontWeight.bold, letterSpacing: 1.5),
        ),
        backgroundColor: goldenBrown,
        foregroundColor: Colors.white,
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 10),

            // Fecha
            Text(
              _formatDate(_now),
              style: const TextStyle(
                fontSize: 20,
                color: Colors.grey,
                fontWeight: FontWeight.w600,
              ),
            ),

            // Reloj
            Text(
              _formatTime(_now),
              style: const TextStyle(
                fontSize: 54,
                fontWeight: FontWeight.bold,
                color: goldenBrown,
                letterSpacing: 2,
              ),
            ),

            const SizedBox(height: 30),

            // Saludo
            Text(
              '¡Bienvenido, ${user?.username ?? "Usuario"}!',
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),

            const SizedBox(height: 40),

            // Spinner de carga inicial O los 4 botones
            if (fichajeVM.status == FichajeStatus.cargando && jornada == null)
              const Expanded(
                child: Center(
                  child: CircularProgressIndicator(color: goldenBrown),
                ),
              )
            else
              Expanded(
                child: Center(
                  child: Wrap(
                    spacing: 24,
                    runSpacing: 24,
                    alignment: WrapAlignment.center,
                    children: [
                      SizedBox(
                        width: 130,
                        height: 130,
                        child: _buildActionBtn(
                          context,
                          label: 'Comienzo\nJornada',
                          icon: Icons.play_circle_fill,
                          iconColor: Colors.green[600]!,
                          isEnabled: tipoActivo == 'INICIO',
                          isLoading: fichajeVM.tipoFichajeCargando == 'INICIO',
                          onTap: () => _onFichar('INICIO'),
                        ),
                      ),
                      SizedBox(
                        width: 130,
                        height: 130,
                        child: _buildActionBtn(
                          context,
                          label: 'Pausa\nAlmuerzo',
                          icon: Icons.restaurant,
                          iconColor: Colors.orange[600]!,
                          isEnabled: tipoActivo == 'ALMUERZO_INICIO',
                          isLoading: fichajeVM.tipoFichajeCargando == 'ALMUERZO_INICIO',
                          onTap: () => _onFichar('ALMUERZO_INICIO'),
                        ),
                      ),
                      SizedBox(
                        width: 130,
                        height: 130,
                        child: _buildActionBtn(
                          context,
                          label: 'Vuelta\nAlmuerzo',
                          icon: Icons.work,
                          iconColor: Colors.blue[600]!,
                          isEnabled: tipoActivo == 'ALMUERZO_FIN',
                          isLoading: fichajeVM.tipoFichajeCargando == 'ALMUERZO_FIN',
                          onTap: () => _onFichar('ALMUERZO_FIN'),
                        ),
                      ),
                      SizedBox(
                        width: 130,
                        height: 130,
                        child: _buildActionBtn(
                          context,
                          label: 'Fin\nJornada',
                          icon: Icons.stop_circle,
                          iconColor: Colors.red[600]!,
                          isEnabled: tipoActivo == 'FIN',
                          isLoading: fichajeVM.tipoFichajeCargando == 'FIN',
                          onTap: () => _onFichar('FIN'),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Future<void> _onFichar(String tipo) async {
    final loginVM  = context.read<LoginViewModel>();
    final fichajeVM = context.read<FichajeViewModel>();
    final user  = loginVM.currentUser;
    final token = await loginVM.getToken();

    if (user != null && token != null) {
      await fichajeVM.fichar(user.id, tipo, token);
    }
  }

  Widget _buildActionBtn(
      BuildContext context, {
        required String label,
        required IconData icon,
        required Color iconColor,
        required bool isEnabled,
        required bool isLoading,
        required VoidCallback onTap,
      }) {
    return ElevatedButton(
      style: ElevatedButton.styleFrom(
        padding: const EdgeInsets.all(8),
        backgroundColor: isEnabled ? Colors.white : Colors.grey[200],
        foregroundColor: goldenBrown,
        elevation: isEnabled ? 6 : 1,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: BorderSide(
            color: isEnabled ? goldenBrown : Colors.grey,
            width: 2,
          ),
        ),
      ),
      onPressed: isEnabled && !isLoading ? onTap : null,
      child: isLoading
          ? const Center(
        child: CircularProgressIndicator(
          color: goldenBrown,
          strokeWidth: 2.5,
        ),
      )
          : Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, size: 38, color: isEnabled ? iconColor : Colors.grey),
          const SizedBox(height: 8),
          Text(
            label,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.bold,
              color: isEnabled ? Colors.black87 : Colors.grey,
              height: 1.1,
            ),
          ),
        ],
      ),
    );
  }
}