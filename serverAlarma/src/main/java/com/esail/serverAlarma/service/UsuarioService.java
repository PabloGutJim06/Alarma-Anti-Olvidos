package com.esail.serverAlarma.service;

import com.esail.serverAlarma.dto.JornadaDTO;
import com.esail.serverAlarma.dto.UsuarioResponseDTO;
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

    /**
     * Verificar si un usuario ya existe en la Base de Datos usando su username
     * @param username El nombre de usuario a comprobar
     * @return true si existe, false si no
     */
    public boolean existeUsuarioPorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean verificarLogin(String username, String password) {
        return usuarioRepository.findByUsername(username).map(usuario -> usuario.getPassword().equals(password)).orElse(false);
    }

    public UsuarioResponseDTO obtenerUsuarioConJornada(String username){
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        List<JornadaDTO> jornadas = usuario.getJornadas().stream()
                .map(j -> new JornadaDTO(j.getId(), j.getDia_semana(),j.getType(),j.getHora_inicio(),j.getHora_fin()))
                .toList();

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.isType(),
                jornadas
        );
    }
}
