package com.esail.serverAlarma.service;

import com.esail.serverAlarma.dto.JornadaDTO;
import com.esail.serverAlarma.dto.JornadaResponseDTO;
import com.esail.serverAlarma.exception.FichajeInvalidoException;
import com.esail.serverAlarma.exception.ResourceNotFoundException;
import com.esail.serverAlarma.exception.YaFichadoException;
import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.JornadaRepository;
import com.esail.serverAlarma.repo.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JornadaService {

    private final JornadaRepository jornadaRepository;
    private final UsuarioRepository usuarioRepository;

    public JornadaService(JornadaRepository jornadaRepository,
                          UsuarioRepository usuarioRepository) {
        this.jornadaRepository = jornadaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ─────────────────────────────────────────
    // MAPPER — entidad → DTO de respuesta
    // Método privado centralizado. Si añades un campo a Jornada,
    // solo lo tocas aquí — no en cada endpoint.
    // ─────────────────────────────────────────
    private JornadaResponseDTO toResponseDTO(Jornada j) {
        JornadaResponseDTO dto = new JornadaResponseDTO();
        dto.setId(j.getId());
        dto.setDia_semana(j.getDia_semana());
        dto.setType(j.getType());
        dto.setHora_inicio(j.getHora_inicio());
        dto.setHoraAlmuerzo(j.getHoraAlmuerzo());
        dto.setHoraVuelta(j.getHoraVuelta());
        dto.setHora_fin(j.getHora_fin());
        dto.setRealInicio(j.getRealInicio());
        dto.setRealAlmuerzoInicio(j.getRealAlmuerzoInicio());
        dto.setRealAlmuerzoFin(j.getRealAlmuerzoFin());
        dto.setRealFin(j.getRealFin());
        return dto;
    }

    // ─────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────
    public JornadaResponseDTO crearJornada(Integer usuarioId, JornadaDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        boolean yaExiste = jornadaRepository
                .findByUsuarioId(usuarioId)
                .stream()
                .anyMatch(j -> j.getDia_semana().equals(dto.getDia_semana()));

        if (yaExiste) {
            throw new IllegalStateException(
                    "Ya existe una jornada para el día " + dto.getDia_semana());
        }

        Jornada jornada = new Jornada();
        jornada.setDia_semana(dto.getDia_semana());
        jornada.setType(dto.getType());
        jornada.setUsuario(usuario);

        jornada.setHora_inicio(
                dto.getHora_inicio() != null ? dto.getHora_inicio() : LocalTime.of(8, 0));
        jornada.setHoraAlmuerzo(
                dto.getHoraAlmuerzo() != null ? dto.getHoraAlmuerzo() : LocalTime.of(14, 0));
        jornada.setHoraVuelta(
                dto.getHoraVuelta() != null ? dto.getHoraVuelta() : LocalTime.of(15, 0));
        jornada.setHora_fin(
                dto.getHora_fin() != null ? dto.getHora_fin() : LocalTime.of(17, 0));

        return toResponseDTO(jornadaRepository.save(jornada));
    }

    // ─────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────
    public JornadaResponseDTO obtenerJornadaPorId(Integer id) {
        return toResponseDTO(
                jornadaRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Jornada", id))
        );
    }

    public List<JornadaResponseDTO> obtenerJornadasDeUsusario(Integer usuarioId) {
        return jornadaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────
    public JornadaResponseDTO actualizarJornada(Integer id, JornadaDTO dto) {
        Jornada existente = jornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada", id));

        existente.setType(dto.getType());
        existente.setHora_inicio(dto.getHora_inicio());
        existente.setHoraAlmuerzo(dto.getHoraAlmuerzo());
        existente.setHoraVuelta(dto.getHoraVuelta());
        existente.setHora_fin(dto.getHora_fin());

        return toResponseDTO(jornadaRepository.save(existente));
    }

    // ─────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────
    public void eliminarJornada(Integer id) {
        Jornada existe = jornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada", id));
        jornadaRepository.delete(existe);
    }

    // ─────────────────────────────────────────
    // FICHAR
    // ─────────────────────────────────────────
    @Transactional
    public JornadaResponseDTO registrarFichajeReal(Integer id, String tipo) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jornada", id));

        LocalTime ahora = LocalTime.now();

        // ── Validación de orden de fichaje ──────────────────────────────────
        // Determinamos cuál es el fichaje esperado en este momento.
        // El usuario solo puede fichar lo que toca ahora — no puede
        // saltar pasos ni fichar eventos del pasado fuera de orden.
        String fichajeEsperado = determinarFichajeEsperado(jornada, ahora);

        if (fichajeEsperado != null && !fichajeEsperado.equals(tipo.toUpperCase())) {
            throw new FichajeInvalidoException(
                    "Ahora solo puedes fichar: " + fichajeEsperado
                            + ". Recibido: " + tipo.toUpperCase()
            );
        }
        // ────────────────────────────────────────────────────────────────────

        switch (tipo.toUpperCase()) {
            case "INICIO":
                if (jornada.getRealInicio() != null) throw new YaFichadoException("INICIO");
                jornada.setRealInicio(ahora);
                break;
            case "ALMUERZO_INICIO":
                if (jornada.getRealAlmuerzoInicio() != null) throw new YaFichadoException("ALMUERZO_INICIO");
                jornada.setRealAlmuerzoInicio(ahora);
                break;
            case "ALMUERZO_FIN":
                if (jornada.getRealAlmuerzoFin() != null) throw new YaFichadoException("ALMUERZO_FIN");
                jornada.setRealAlmuerzoFin(ahora);
                break;
            case "FIN":
                if (jornada.getRealFin() != null) throw new YaFichadoException("FIN");
                jornada.setRealFin(ahora);
                break;
            default:
                throw new FichajeInvalidoException(tipo);
        }

        return toResponseDTO(jornadaRepository.save(jornada));
    }

    /**
     * Determina cuál es el único fichaje válido en este momento.
     * Sigue la misma lógica de prioridad que el sistema de notificaciones:
     * del más urgente (FIN) al menos urgente (INICIO).
     *
     * Devuelve null si no hay ningún fichaje urgente aún
     * (todas las horas previstas están en el futuro).
     */
    private String determinarFichajeEsperado(Jornada jornada, LocalTime ahora) {

        if (jornada.getRealFin() == null
                && jornada.getHora_fin() != null
                && jornada.getHora_fin().isBefore(ahora)) {
            return "FIN";
        }

        if (jornada.getRealAlmuerzoFin() == null
                && jornada.getHoraVuelta() != null
                && jornada.getHoraVuelta().isBefore(ahora)) {
            return "ALMUERZO_FIN";
        }

        if (jornada.getRealAlmuerzoInicio() == null
                && jornada.getHoraAlmuerzo() != null
                && jornada.getHoraAlmuerzo().isBefore(ahora)) {
            return "ALMUERZO_INICIO";
        }

        if (jornada.getRealInicio() == null
                && jornada.getHora_inicio() != null
                && jornada.getHora_inicio().isBefore(ahora)) {
            return "INICIO";
        }

        // Ninguna hora prevista ha pasado aún — no hay fichaje urgente
        return null;
    }

    // obtenerTodasLasJornadas() — uso interno, no expuesto al controller
    public List<Jornada> obtenerTodasLasJornadas() {
        return jornadaRepository.findAll();
    }
}