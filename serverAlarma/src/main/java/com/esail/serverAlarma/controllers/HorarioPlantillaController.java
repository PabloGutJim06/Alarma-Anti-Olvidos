package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.dto.HorarioPlantillaRequestDTO;
import com.esail.serverAlarma.dto.JornadaResponseDTO;
import com.esail.serverAlarma.models.HorarioPlantilla;
import com.esail.serverAlarma.service.HorarioPlantillaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/horario")
public class HorarioPlantillaController {

    private final HorarioPlantillaService plantillaService;

    public HorarioPlantillaController(HorarioPlantillaService plantillaService) {
        this.plantillaService = plantillaService;
    }

    /**
     * Ver la plantilla semanal de un usuario.
     * GET /api/usuarios/1/horario
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<HorarioPlantilla> obtenerHorario(@PathVariable Integer usuarioId) {
        return plantillaService.obtenerPlantillasUsuario(usuarioId);
    }

    /**
     * Generar la Jornada de hoy desde la plantilla.
     * POST /api/usuarios/1/horario/generar-hoy
     */
    @PostMapping("/generar-hoy")
    @ResponseStatus(HttpStatus.CREATED)
    public JornadaResponseDTO generarJornadaHoy(@PathVariable Integer usuarioId) {
        return plantillaService.generarJornadaDeHoy(usuarioId);
    }
//    @PutMapping("/{diaSemana}")
//    @ResponseStatus(HttpStatus.OK)
//    public HorarioPlantilla actualizarHorario(
//            @PathVariable Integer usuarioId,
//            @PathVariable DayOfWeek diaSemana,
//            @RequestBody HorarioPlantillaRequestDTO dto) {
//        return plantillaService.actualizarPlantilla(usuarioId, diaSemana, dto);
//    }
}