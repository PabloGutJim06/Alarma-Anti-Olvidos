package com.esail.serverAlarma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // El servidor envía mensajes a los clientes por aquí
        config.enableSimpleBroker("/topic");
        // El cliente envía mensajes al servidor por aquí
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // El punto de conexión (el "enchufe")
        registry.addEndpoint("/ws-alarma")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
