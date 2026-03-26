package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Usuario> obtenerTodosLosUsuarios(){
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuariosPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Esto tiene una forma más cutre de hacerlo ->
    /*
    List<Usuario> usuarios = usuarioRepository.findAll();
        for(Usuario usuario : usuarios){
            if(usuario.getUsername().equalsIgnoreCase(username)){
                return usuario;
            }
        }
        return null;
     */

    public Usuario obtenerUsuarioPorId(Integer id){
        return usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuario(Integer id, Usuario datos) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUsername(datos.getUsername());
        usuario.setPassword(datos.getPassword());
        usuario.setType(datos.isType());

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuarioPorUserName(String usuario, Usuario datos) {
        Usuario u = usuarioRepository.findByUsername(usuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setUsername(datos.getUsername());
        u.setPassword(datos.getPassword());
        u.setType(datos.isType());

        return usuarioRepository.save(u);
    }

    public void eliminarUsuarioPorId(Integer id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(u);
    }

    public void eliminarUsuarioPorUsername(String username) {
        Usuario u = usuarioRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(u);
    }


}
