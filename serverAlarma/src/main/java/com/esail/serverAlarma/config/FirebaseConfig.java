package com.esail.serverAlarma.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // Leemos la ruta desde el application.properties
    @Value("${firebase.config.path}")
    private Resource firebaseResource;

    @PostConstruct // Se ejecuta automáticamente al arrancar el servidor
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado con éxito.");
            }
        } catch (IOException e) {
            // Esto hará que el servidor EXPLOTE al arrancar si el archivo no está
            // Es mejor que falle aquí que luego en mitad de una notificación
            throw new RuntimeException("CRÍTICO: No se pudo cargar el archivo de Firebase. Revisa la ruta y el nombre.", e);
        }
    }
}
