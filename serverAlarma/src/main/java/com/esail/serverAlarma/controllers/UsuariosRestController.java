package com.esail.serverAlarma.controllers;

import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.service.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Es lo conocido como API
@RequestMapping("/api/usuarios")
public class UsuariosRestController {

    private final UsuarioService usuarioService;

    public UsuariosRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isType()
        );
    }

}
