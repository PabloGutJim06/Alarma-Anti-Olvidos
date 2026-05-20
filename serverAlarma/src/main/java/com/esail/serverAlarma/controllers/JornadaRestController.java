package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.dto.JornadaDTO;
import com.esail.serverAlarma.dto.JornadaResponseDTO;
import com.esail.serverAlarma.service.JornadaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/jornadas")
public class JornadaRestController {

    private final JornadaService jornadaService;

    public JornadaRestController(JornadaService jornadaService) {
        this.jornadaService = jornadaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JornadaResponseDTO crear(@PathVariable Integer usuarioId,
                                    @RequestBody JornadaDTO dto) {
        return jornadaService.crearJornada(usuarioId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<JornadaResponseDTO> obtenerTodasJornadaUsuario(@PathVariable Integer usuarioId) {
        return jornadaService.obtenerJornadasDeUsusario(usuarioId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JornadaResponseDTO obtenerJornadaPorId(@PathVariable Integer id) {
        return jornadaService.obtenerJornadaPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JornadaResponseDTO actualizarJornadaPorId(@PathVariable Integer id,
                                                     @RequestBody JornadaDTO dto) {
        return jornadaService.actualizarJornada(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarJornadaPorId(@PathVariable Integer id) {
        jornadaService.eliminarJornada(id);
    }

    @PostMapping("/{id}/fichar")
    @ResponseStatus(HttpStatus.OK)
    public JornadaResponseDTO fichar(@PathVariable Integer id,
                                     @RequestParam String tipo) {
        return jornadaService.registrarFichajeReal(id, tipo);
    }
}