package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //Es lo conocido como API
@RequestMapping("/api/usuarios")
public class UsuariosRestController {

    private final UsuarioService usuarioService;

    public UsuariosRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Metodo Create (CRUD). Crea un usuario en la BD
     * @param usuario
     * @return nuevo Usuario al sistema
     */
    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isType()
        );
    }

    /**
     * Metodo Read (CRUD). Le y muestra una lista con todos los usuarios del sistema
     * @return List con todos los usuarios del sistema
     */
    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    /**
     * Metodo Read (CRUD). Devuelve un usuario (Busca el usuario por nombre)
     * @param username
     * @return un usuario que conincida con el nombre
     */
    @GetMapping("/{username:[a-zA-Z][a-zA-Z0-9_]*}") //Como los dos GET comparten el mismo EndPoint, Surge un problema de ambigüedad, que se soluciona Cambiando el EndPoint o con una Expresión Regular
    public Usuario obtenerUsuarioPorUsername(@PathVariable String username) {
        return usuarioService.obtenerUsuariosPorUsername(username);
    }

    /**
     * Metodo Read (CRUD). Devuelve un usuario (Busca por el ID del usuario)
     * @param id
     * @return un usuario que coincida con el ID que se ha introducido
     */
    @GetMapping("/{id:\\d+}")
    public Usuario obtenerUsuarioPorId(@PathVariable Integer id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }

    /**
     * Metodo Update (CRUD). Modifica un usuario (Busca por el ID del usuario)
     * @param id
     * @param usuario
     * @return los cambios que se le aplicaran a x usuario
     */
    @PutMapping("/{id:\\d+}")
    public Usuario actualizarUsuarioPorId(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    /**
     * metodo Update (CRUD). Modifica un usuario (Busca por el userName del usuario)
     * @param username
     * @param usuario
     * @return los cambios que se le aplicaran a x usuario
     */
    @PutMapping("/{username:[a-zA-Z][a-zA-Z0-9_]*}")
    public Usuario actualizarUsuarioPorUsername(@PathVariable String username, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuarioPorUserName(username, usuario);
    }

    /**
     * Metodo Delete(CRUD). Elimina un usuario del sistema (Busca por el ID del usuario)
     * @param id
     */
    @DeleteMapping("/{id:\\d+}")
    public void eliminarUsuarioPorId(@PathVariable Integer id) {
        usuarioService.eliminarUsuarioPorId(id);
    }

    /**
     * Metodo Delete(CRUD). Elimina un usuario del sistema (Busca por el Username del usuario)
     * @param username
     */
    @DeleteMapping("/{username:[a-zA-Z][a-zA-Z0-9_]*}")
    public void eliminarUsuarioPorUsername(@PathVariable String username) {
        usuarioService.eliminarUsuarioPorUsername(username);
    }

    /**
     * Endpoint para comprobar si un usuario existe
     * @param username El nombre de usuario que viaja en la URL
     * @return true o false
     */
    @GetMapping("/existe/{username}")
    public boolean verificarUsuarioPorUsername(@PathVariable String username) {
        return usuarioService.existeUsuarioPorUsername(username);
    }
}
