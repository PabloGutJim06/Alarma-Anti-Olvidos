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
        Usuario usuario = jornada.getUsuario();

        // FIN
        if (jornada.getRealFin() == null
                && jornada.getHora_fin() != null
                && jornada.getHora_fin().isBefore(ahora.toLocalTime())) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado la salida!", ahora,
                    jornada.getHora_fin(),                // ← hora prevista
                    jornada::getUltimoAvisoFin,
                    jornada::setUltimoAvisoFin
            );
            if (disparado) jornadaRepository.save(jornada);
            return;
        }

        // ALMUERZO FIN
        if (jornada.getRealAlmuerzoFin() == null
                && jornada.getHoraVuelta() != null
                && jornada.getHoraVuelta().isBefore(ahora.toLocalTime())) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado la vuelta del almuerzo!", ahora,
                    jornada.getHoraVuelta(),              // ← hora prevista
                    jornada::getUltimoAvisoAlmuerzoFin,
                    jornada::setUltimoAvisoAlmuerzoFin
            );
            if (disparado) jornadaRepository.save(jornada);
            return;
        }

        // ALMUERZO INICIO
        if (jornada.getRealAlmuerzoInicio() == null
                && jornada.getHoraAlmuerzo() != null
                && jornada.getHoraAlmuerzo().isBefore(ahora.toLocalTime())) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado el inicio del almuerzo!", ahora,
                    jornada.getHoraAlmuerzo(),            // ← hora prevista
                    jornada::getUltimoAvisoAlmuerzoInicio,
                    jornada::setUltimoAvisoAlmuerzoInicio
            );
            if (disparado) jornadaRepository.save(jornada);
            return;
        }

        // INICIO
        if (jornada.getRealInicio() == null
                && jornada.getHora_inicio() != null
                && jornada.getHora_inicio().isBefore(ahora.toLocalTime())) {

            boolean disparado = notificarSiProcede(
                    usuario, "¡No has fichado la entrada!", ahora,
                    jornada.getHora_inicio(),             // ← hora prevista
                    jornada::getUltimoAvisoInicio,
                    jornada::setUltimoAvisoInicio
            );
            if (disparado) jornadaRepository.save(jornada);
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