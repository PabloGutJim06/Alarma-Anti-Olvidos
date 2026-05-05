package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Registro;
import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.JornadaRepository;
import com.esail.serverAlarma.repo.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificacionService {
    @Autowired
    private WindowsNotificationService windowsService;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private RegistroRepository registroRepository;

    //Hacer que se ejecute cada minuto
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void revisarOlvidosDeFichaje() {
        LocalTime ahora = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDate hoy = LocalDate.now();

        // Buscamos jornadas que empezaron hace 10 minutos
        LocalTime horaObjetivo = ahora.minusMinutes(10);

        List<Jornada> jornadasAControlar = jornadaRepository.findByDiaSemanaAndHoraInicio(hoy, horaObjetivo);

        for (Jornada jornada : jornadasAControlar) {
            boolean fichado = registroRepository.existsByJornadaIdAndTituloContainingIgnoreCase(jornada.getId(), "Inicio");

            if (!fichado) {
                // 2. Llamamos al método híbrido que gestiona ambos canales
                enviarNotificacionEmergente(jornada.getUsuario());
            }
        }
    }

    private void enviarNotificacionEmergente(Usuario usuario) {
        String titulo = "¡Alarma Anti-Olvidos!";
        String mensaje = "Hola " + usuario.getUsername() + ", parece que no has fichado.";

        // 1. Intentar por Windows (vía WebSocket)
        // La app de Windows estará escuchando en /topic/notificaciones/nombreUsuario
        windowsService.enviarNotificacionWindows(usuario.getUsername(), titulo, mensaje);

        // 2. Intentar por Firebase (Móvil)
        String token = usuario.getDeviceToken();
        if (token != null && !token.isEmpty()) {
            enviarNotificacionFirebase(token, titulo, mensaje);
        } else {
            System.out.println("Aviso: El usuario " + usuario.getUsername() + " no tiene token de Firebase.");
        }
    }


    private void enviarNotificacionFirebase(String token, String titulo, String mensaje) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(titulo)
                    .setBody(mensaje)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.err.println("Error en Firebase: " + e.getMessage());
        }
    }
}
