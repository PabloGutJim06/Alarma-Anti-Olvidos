package com.esail.serverAlarma.service;

import com.esail.serverAlarma.dto.JornadaResponseDTO;
import com.esail.serverAlarma.exception.ResourceNotFoundException;
import com.esail.serverAlarma.models.HorarioPlantilla;
import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.HorarioPlantillaRepository;
import com.esail.serverAlarma.repo.JornadaRepository;
import com.esail.serverAlarma.repo.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class HorarioPlantillaService {

    private final HorarioPlantillaRepository plantillaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JornadaRepository jornadaRepository;
    private final JornadaService jornadaService;

    public HorarioPlantillaService(HorarioPlantillaRepository plantillaRepository,
                                   UsuarioRepository usuarioRepository,
                                   JornadaRepository jornadaRepository,
                                   JornadaService jornadaService) {
        this.plantillaRepository = plantillaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jornadaRepository = jornadaRepository;
        this.jornadaService = jornadaService;
    }

    /**
     * Devuelve todas las plantillas activas de un usuario.
     */
    public List<HorarioPlantilla> obtenerPlantillasUsuario(Integer usuarioId) {
        return plantillaRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    /**
     * Genera la Jornada de HOY para un usuario concreto
     * leyendo su plantilla del día de la semana actual.
     *
     * Si no hay plantilla activa para hoy → lanza excepción clara.
     * Si ya existe una Jornada para hoy → lanza excepción clara.
     */
    @Transactional
    public JornadaResponseDTO generarJornadaDeHoy(Integer usuarioId) {
        LocalDate hoy = LocalDate.now();
        DayOfWeek diaSemana = hoy.getDayOfWeek();

        // 1. Buscar la plantilla del día
        HorarioPlantilla plantilla = plantillaRepository
                .findByUsuarioIdAndDiaSemana(usuarioId, diaSemana)
                .filter(HorarioPlantilla::isActivo)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay plantilla activa para " + diaSemana + " del usuario " + usuarioId));

        // 2. Comprobar que no existe ya una Jornada para hoy
        boolean yaExiste = jornadaRepository
                .findByUsuarioId(usuarioId)
                .stream()
                .anyMatch(j -> j.getDia_semana().equals(hoy));

        if (yaExiste) {
            throw new IllegalStateException(
                    "Ya existe una jornada para hoy (" + hoy + ") del usuario " + usuarioId);
        }

        // 3. Construir la Jornada desde la plantilla
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        Jornada jornada = new Jornada();
        jornada.setDia_semana(hoy);
        jornada.setType("Completa"); // tipo por defecto — se puede parametrizar
        jornada.setUsuario(usuario);
        jornada.setHora_inicio(plantilla.getHoraInicio());
        jornada.setHoraAlmuerzo(plantilla.getHoraAlmuerzo());
        jornada.setHoraVuelta(plantilla.getHoraVuelta());
        jornada.setHora_fin(plantilla.getHoraFin());

        return jornadaService.toResponseDTO(jornadaRepository.save(jornada));
    }

    /**
     * Genera la Jornada de HOY para TODOS los usuarios
     * que tengan plantilla activa para el día actual.
     * Usuarios sin plantilla o con jornada ya creada se saltan silenciosamente.
     */
    @Transactional
    public List<String> generarJornadasDeHoyParaTodos() {
        LocalDate hoy = LocalDate.now();
        DayOfWeek diaSemana = hoy.getDayOfWeek();

        // Todos los usuarios con plantilla activa hoy
        List<HorarioPlantilla> plantillasDeHoy =
                plantillaRepository.findByDiaSemanaAndActivoTrue(diaSemana);

        List<String> resultado = new ArrayList<>();

        for (HorarioPlantilla plantilla : plantillasDeHoy) {
            Integer usuarioId = plantilla.getUsuario().getId();
            String username = plantilla.getUsuario().getUsername();

            // ¿Ya tiene jornada hoy?
            boolean yaExiste = jornadaRepository
                    .findByUsuarioId(usuarioId)
                    .stream()
                    .anyMatch(j -> j.getDia_semana().equals(hoy));

            if (yaExiste) {
                resultado.add("⏭️ " + username + " — ya tenía jornada para hoy");
                continue;
            }

            // Crear la jornada desde la plantilla
            Jornada jornada = new Jornada();
            jornada.setDia_semana(hoy);
            jornada.setType("Completa");
            jornada.setUsuario(plantilla.getUsuario());
            jornada.setHora_inicio(plantilla.getHoraInicio());
            jornada.setHoraAlmuerzo(plantilla.getHoraAlmuerzo());
            jornada.setHoraVuelta(plantilla.getHoraVuelta());
            jornada.setHora_fin(plantilla.getHoraFin());

            jornadaRepository.save(jornada);
            resultado.add("✅ " + username + " — jornada creada: "
                    + plantilla.getHoraInicio() + " → " + plantilla.getHoraFin());
        }

        return resultado;
    }
    /**
     * Genera automáticamente las jornadas de hoy para todos los usuarios
     * cada día laborable a las 06:00 AM.
     *
     * Cron: "0 0 6 * * MON-FRI"
     *   └── segundos minutos horas día-mes mes día-semana
     */
    @Scheduled(cron = "0 0 6 * * MON-FRI")
    public void generarJornadasAutomaticamente() {
        System.out.println("🕕 [Scheduler] Generando jornadas automáticas para hoy...");
        List<String> resultado = generarJornadasDeHoyParaTodos();
        resultado.forEach(System.out::println);
    }

    /**
     * Recuperación — cada 5 min, solo si faltan jornadas.
     * Actúa como seguro si el servidor no estaba a las 6 AM.
     */
    @Scheduled(fixedRate = 300000, initialDelay = 60000)
    public void recuperarJornadasSiFaltan() {
        LocalDate hoy = LocalDate.now();
        DayOfWeek diaSemana = hoy.getDayOfWeek();

        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) return;

        List<HorarioPlantilla> plantillasDeHoy =
                plantillaRepository.findByDiaSemanaAndActivoTrue(diaSemana);

        if (plantillasDeHoy.isEmpty()) return;

        long jornadasExistentes = plantillasDeHoy.stream()
                .map(p -> p.getUsuario().getId())
                .filter(usuarioId -> jornadaRepository
                        .findByUsuarioId(usuarioId)
                        .stream()
                        .anyMatch(j -> j.getDia_semana().equals(hoy)))
                .count();

        if (jornadasExistentes >= plantillasDeHoy.size()) return;

        System.out.println("⚠️ [Recuperación] Faltan jornadas para hoy. Generando...");
        generarJornadasDeHoyParaTodos().forEach(System.out::println);
    }
}