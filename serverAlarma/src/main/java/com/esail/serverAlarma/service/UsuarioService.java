package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service //Donde va la lógica del proyecto
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(String username, String password, boolean type) {
        Usuario usuario = new Usuario(username, password, type);
        return usuarioRepository.save(usuario);
    }
}
