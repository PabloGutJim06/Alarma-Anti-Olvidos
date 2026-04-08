package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.service.JornadaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/usuarios/{usuarioId}/jornadas")
public class JornadaRestController {
    private final JornadaService jornadaService;

    // Inyección de dependencias por constructor (nuestra alianza inquebrantable)
    public JornadaRestController(JornadaService jornadaService) {
        this.jornadaService = jornadaService;
    }

    /**
     * Metodo Create (CRUD). Añade una nueva jornada a un usuario existente.
     * @param usuarioId El ID del usuario capturado de la URL.
     * @param jornada El objeto JSON con los datos de la jornada (fechaInicio, etc.).
     * @return La nueva Jornada creada en la base de datos.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // ¡Buena práctica! Devuelve un 201 en lugar de un 200
    public Jornada crear(@PathVariable Integer usuarioId, @RequestBody Jornada jornada) {
        // Llamamos al metodo que preparamos en nuestra sesión anterior
        return jornadaService.crearJornada(usuarioId, jornada);
    }

    /**
     * Metodo Read (CRUD). Le y muestra una lista con todos las jornadas de un usuario
     * @return un listado con todas las jornadas de x usuario
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Jornada> obtenerTodasJornadaUsuario(@PathVariable Integer usuarioId) {
        return jornadaService.obtenerJornadasDeUsusario(usuarioId);
    }

    /**
     * Metodo Read (CRUD). Devuelve una Jornada (Busca por el ID de la jornada)
     * @param id es la clave primaria de cada jornada
     * @return una jornada que coincida con el id
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Jornada obtenerJornadaPorId(@PathVariable Integer id) {
        return jornadaService.obtenerJornadaPorId(id);
    }

    /**
     * Metodo Update (CRUD). Modifica una Jornada (Busca por el ID)
     * @param id es la clave primaria por la que buscará
     * @param jornada Es ol objeto JSON que recive
     * @return el nuevo Objeto con los cambios aplicados
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Jornada actualizarJornadaPorId(@PathVariable Integer id, @RequestBody Jornada jornada) {
        return jornadaService.actualizarJornada(id, jornada);
    }

    /**
     * Metodo Delete(CRUD). Elimina una jornada del sistema (Busca por el ID)
     * @param id es la clave primaria, por la que se busca la jornada que se va a borrar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarJornadaPorId(@PathVariable Integer id) {
        jornadaService.eliminarJornada(id);
    }
}
