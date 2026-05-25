package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.service.HorarioPlantillaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horario")
public class HorarioGlobalController {

    private final HorarioPlantillaService plantillaService;

    public HorarioGlobalController(HorarioPlantillaService plantillaService) {
        this.plantillaService = plantillaService;
    }

    /**
     * Genera las jornadas de HOY para todos los usuarios con plantilla activa.
     * POST /api/horario/generar-hoy-todos
     */
    @PostMapping("/generar-hoy-todos")
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> generarJornadasHoyParaTodos() {
        return plantillaService.generarJornadasDeHoyParaTodos();
    }
}