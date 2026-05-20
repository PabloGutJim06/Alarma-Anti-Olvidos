package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.repo.JornadaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Solo responsable de la patrulla periódica.
 * Sin @Transactional — la transacción vive en JornadaNotificacionProcessor,
 * una por jornada, independientes entre sí.
 */
@Service
public class NotificacionService {

    private final JornadaRepository jornadaRepository;
    private final JornadaNotificacionProcessor processor;

    public NotificacionService(JornadaRepository jornadaRepository,
                               JornadaNotificacionProcessor processor) {
        this.jornadaRepository = jornadaRepository;
        this.processor = processor;
    }

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void revisarOlvidosDeFichaje() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();

        List<Jornada> candidatos = jornadaRepository
                .findCandidatosOlvido(hoy, ahora.toLocalTime());

        System.out.println("🔍 [Scheduler] " + ahora + " — candidatos encontrados: " + candidatos.size());

        for (Jornada jornada : candidatos) {
            try {
                processor.procesar(jornada, ahora);
            } catch (Exception e) {
                // Si una jornada falla, logamos y continuamos con la siguiente.
                // El bucle no muere por un error puntual.
                System.err.println("Error procesando jornada "
                        + jornada.getId() + ": " + e.getMessage());
            }
        }
    }
}