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
    private JornadaRepository jornadaRepository;

    @Autowired
    private RegistroRepository registroRepository;

    //Hacer que se ejecute cada minuto
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void revisarOlvidosDeFichaje(){
        LocalTime ahora  = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDate hoy =  LocalDate.now();

        // --- Buscamos jornadas que empezaron hace 10 minutos ---
        LocalTime horaObjetivo = ahora.minusMinutes(10);

        System.out.println("Buscando jornadas para hoy a las: " + horaObjetivo);
        List<Jornada> jornadasAControlar = jornadaRepository.findByDiaSemanaAndHoraInicio(hoy, horaObjetivo);
        System.out.println("Jornadas ecnontradas: " + jornadasAControlar.size());

        for(Jornada jornada : jornadasAControlar){
            boolean fichado = registroRepository.existsByJornadaIdAndTituloContainingIgnoreCase(jornada.getId(), "Inicio");

            if (!fichado){
                 enviarNotificacionEmergente(jornada.getUsuario());
            }
        }
    }
    private void enviarNotificacionEmergente(Usuario usuario){
        // Comprobamos si el usuario tiene un token guardado
        String token = usuario.getDeviceToken();
        if (token == null || token.isEmpty()) {
            System.out.println("El usuario " + usuario.getUsername() + " no tiene token de dispositivo registrado. No se puede enviar push. ");
            return;
        }

        try{
            Notification notification = Notification.builder()
                    .setTitle("¡Alarma Anti-Olvidos!")
                    .setBody("Hola " + usuario.getUsername() + ", parece que tu jornada ha empezado pero no has fichado. ¡Entra a la app!")
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .putData("tipoAlarma", "inicio_jornada") // Puedes enviar datos "invisibles" extra aquí
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notificación enviada con éxito a " + usuario.getUsername() + ". ID del mensaje: " + response);
        }catch (Exception e) {
            System.err.println("Error al enviar la notificación a " + usuario.getUsername() + ": " + e.getMessage());
        }
    }
}
