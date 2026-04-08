package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.models.Registro;
import com.esail.serverAlarma.service.RegistroService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/jornadas/{jornadaId}/registros")
public class RegistroRestController {
    private final RegistroService registroService;

    public RegistroRestController(RegistroService registroService) {
        this.registroService = registroService;
    }

    /**
     * Metodo Create (CRUD). Añade una nueva jornada a un usuario existente.
     * @param jornadaId El ID de la jornada capturado de la URL.
     * @param registro El objeto JSON con los datos del registro.
     * @return El nuevo registro creado en la base de datos.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Registro crear(@PathVariable Integer jornadaId, @RequestBody Registro registro) {
        return registroService.crearRegistro(jornadaId, registro);
    }

    /**
     * Metodo Read (CRUD). Le y muestra una lista con todos los registros de una jornada
     * @return un listado con todas los registros de x jornada
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Registro> obtenerTodosLosRegistrosJornmadas(@PathVariable Integer jornadaId) {
        return registroService.obtenerRegistrosJornada(jornadaId);
    }

    /**
     * Metodo Read (CRUD). Devuelve un registro (Busca por el ID del registro)
     * @param id es la clave primaria de cada registro
     * @return un registro que coincida con el id
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Registro obtenerRegistroPorId(@PathVariable Integer id) {
        return registroService.obtenerRegistroPorId(id);
    }

    /**
     * Metodo Update (CRUD). Modifica un registro (Busca por el ID)
     * @param id es la clave primaria por la que buscará
     * @param registro Es ol objeto JSON que recive
     * @return el nuevo Objeto con los cambios aplicados
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Registro actualizarRegistroPorId(@PathVariable Integer id, @RequestBody Registro registro) {
        return registroService.actualizarRegistroPorId(id, registro);
    }

    /**
     * Metodo Delete(CRUD). Elimina un registro del sistema (Busca por el ID)
     * @param id es la clave primaria, por la que se busca el registro que se va a borrar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarRegistroPorId(@PathVariable Integer id) {
        registroService.eliminarRegistro(id);
    }

}
