// Nuevo fichero: JornadaNotificacionProcessor.java
package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.JornadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Procesa UNA jornada por transacción.
 * Separado de NotificacionService para que @Transactional
 * pase por el proxy de Spring correctamente.
 */
@Service
public class JornadaNotificacionProcessor {

    private final JornadaRepository jornadaRepository;
    private final WindowsNotificationService windowsService;

    private static final long MINUTOS_ENTRE_AVISOS = 10;

    public JornadaNotificacionProcessor(JornadaRepository jornadaRepository,
                                        WindowsNotificationService windowsService) {
        this.jornadaRepository = jornadaRepository;
        this.windowsService = windowsService;
    }

    /**
     * Cada llamada a este método es una transacción independiente.
     * Si Firebase falla en la jornada 30, las jornadas 1-29 ya están
     * commiteadas y no se ven afectadas.
     */
    @Transactional
    public void procesar(Jornada jornada, LocalDateTime ahora) {
        Jornada j = jornadaRepository.findById(jornada.getId()).orElse(null);
        if (j == null) return;

        Usuario usuario = jornada.getUsuario();
        LocalTime ahoraTime = ahora.toLocalTime();

        // FIN — solo si el anterior (ALMUERZO_FIN) ya está fichado
        // O si horaVuelta no ha pasado (jornada sin almuerzo)
        if (j.getRealFin() == null
                && j.getHora_fin() != null
                && j.getHora_fin().isBefore(ahoraTime)
                && (j.getRealAlmuerzoFin() != null
                || j.getHoraVuelta() == null
                || !j.getHoraVuelta().isBefore(ahoraTime))) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado la salida!", ahora,
                    j.getHora_fin(), j::getUltimoAvisoFin, j::setUltimoAvisoFin);
            System.out.println("🔔 [Processor] FIN disparado=" + disparado);
            if (disparado) jornadaRepository.save(j);
            return;
        }

        // ALMUERZO_FIN — solo si el anterior (ALMUERZO_INICIO) ya está fichado
        // O si horaAlmuerzo no ha pasado (jornada sin almuerzo)
        if (j.getRealAlmuerzoFin() == null
                && j.getHoraVuelta() != null
                && j.getHoraVuelta().isBefore(ahoraTime)
                && (j.getRealAlmuerzoInicio() != null
                || j.getHoraAlmuerzo() == null
                || !j.getHoraAlmuerzo().isBefore(ahoraTime))) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado la vuelta del almuerzo!", ahora,
                    j.getHoraVuelta(), j::getUltimoAvisoAlmuerzoFin, j::setUltimoAvisoAlmuerzoFin);
            System.out.println("🔔 [Processor] ALMUERZO_FIN disparado=" + disparado);
            if (disparado) jornadaRepository.save(j);
            return;
        }

        // ALMUERZO_INICIO — solo si el anterior (INICIO) ya está fichado
        // O si hora_inicio no ha pasado
        if (j.getRealAlmuerzoInicio() == null
                && j.getHoraAlmuerzo() != null
                && j.getHoraAlmuerzo().isBefore(ahoraTime)
                && (j.getRealInicio() != null
                || j.getHora_inicio() == null
                || !j.getHora_inicio().isBefore(ahoraTime))) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado el inicio del almuerzo!", ahora,
                    j.getHoraAlmuerzo(), j::getUltimoAvisoAlmuerzoInicio, j::setUltimoAvisoAlmuerzoInicio);
            System.out.println("🔔 [Processor] ALMUERZO_INICIO disparado=" + disparado);
            if (disparado) jornadaRepository.save(j);
            return;
        }

        // INICIO — siempre es el primero, sin condición de anterior
        if (j.getRealInicio() == null
                && j.getHora_inicio() != null
                && j.getHora_inicio().isBefore(ahoraTime)) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado la entrada!", ahora,
                    j.getHora_inicio(), j::getUltimoAvisoInicio, j::setUltimoAvisoInicio);
            System.out.println("🔔 [Processor] INICIO disparado=" + disparado);
            if (disparado) jornadaRepository.save(j);
        }
    }

    private boolean notificarSiProcede(Usuario usuario,
                                       String mensajeCuerpo,
                                       LocalDateTime ahora,
                                       LocalTime horaPrevista,        // ← nuevo
                                       Supplier<LocalDateTime> getter,
                                       Consumer<LocalDateTime> setter) {

        LocalDateTime ultimaVez = getter.get();
        long minutosDesdePrevista = Duration.between(
                horaPrevista.atDate(ahora.toLocalDate()), ahora).toMinutes();

        boolean puedeNotificar;

        if (ultimaVez == null) {
            // Primer aviso: solo si han pasado al menos 10 min desde la hora prevista
            System.out.println("⏱️ [Notificacion] minutosDesdePrevista=" + minutosDesdePrevista
                    + " — umbral=" + MINUTOS_ENTRE_AVISOS
                    + " — puedeNotificar=" + (minutosDesdePrevista >= MINUTOS_ENTRE_AVISOS));
            puedeNotificar = minutosDesdePrevista >= MINUTOS_ENTRE_AVISOS;
        } else {
            long minDesdeUltimo = Duration.between(ultimaVez, ahora).toMinutes();
            System.out.println("⏱️ [Notificacion] minDesdeUltimoAviso=" + minDesdeUltimo
                    + " — puedeNotificar=" + (minDesdeUltimo >= MINUTOS_ENTRE_AVISOS));
            puedeNotificar = minDesdeUltimo >= MINUTOS_ENTRE_AVISOS;
        }

        if (!puedeNotificar) return false;

        String titulo = "⏰ Fichaje pendiente";
        String cuerpo = "Hola " + usuario.getUsername() + ", " + mensajeCuerpo;

        windowsService.enviarNotificacionWindows(usuario.getUsername(), titulo, cuerpo);

        String token = usuario.getDeviceToken();
        if (token != null && !token.isBlank()) {
            enviarNotificacionFirebase(token, titulo, cuerpo);
        }

        setter.accept(ahora);
        return true;
    }

    private void enviarNotificacionFirebase(String token, String titulo, String mensaje) {
        try {
            com.google.firebase.messaging.Notification notification =
                    com.google.firebase.messaging.Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build();

            com.google.firebase.messaging.Message message =
                    com.google.firebase.messaging.Message.builder()
                            .setToken(token)
                            .setNotification(notification)
                            .build();

            com.google.firebase.messaging.FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.err.println("Error en Firebase: " + e.getMessage());
        }
    }
}