package com.esail.serverAlarma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Le dice a Spring que lea esto al arrancar la fortaleza
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica a todas las rutas que empiecen por /api/
                .allowedOrigins("http://localhost:3050") // SOLO permite a tu app de Flutter
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite estos métodos (OPTIONS es vital para navegadores)
                .allowedHeaders("*") // Permite cualquier cabecera (incluyendo el JSON)
                .allowCredentials(true); // Necesario si en el futuro usas Cookies o Tokens de sesión
    }
}
