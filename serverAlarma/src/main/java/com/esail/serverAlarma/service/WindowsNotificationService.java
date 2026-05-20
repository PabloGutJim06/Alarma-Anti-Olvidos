package com.esail.serverAlarma.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WindowsNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WindowsNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enviarNotificacionWindows(String username, String titulo, String mensaje) {
        // Enviamos el mensaje al canal privado de ese usuario
        String destino = "/topic/notificaciones/" + username;
        Map<String, String> payload = new HashMap<>();
        payload.put("title", titulo);
        payload.put("body", mensaje);

        messagingTemplate.convertAndSend(destino, payload);
        System.out.println("🖥️ Notificación enviada vía WebSocket a: " + username);
    }
}