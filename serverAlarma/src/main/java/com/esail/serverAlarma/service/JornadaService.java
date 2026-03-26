package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Usuario;
import com.esail.serverAlarma.repo.JornadaRepository;
import com.esail.serverAlarma.repo.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JornadaService {
    private final JornadaRepository jornadaRepository;
    private final UsuarioRepository usuarioRepository;

    public JornadaService(JornadaRepository jornadaRepository, UsuarioRepository usuarioRepository) {
        this.jornadaRepository = jornadaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * --- CREATE ---
     * Recibimos el ID del usuario al que le pertenece la jornada y los datos de la jornada
     * @param usuarioId ID del usuario
     * @param jornada Objeto Jornada
     * @return el objeto jornada que se guarda.
     */
    public Jornada crearJornada(Integer usuarioId, Jornada jornada){
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        jornada.setUsuario(usuario);
        return jornadaRepository.save(jornada);
    }

    /**
     * --- READ ---
     * @return Una lista con toodos los objetos de tipo jornada que se encuentren en la BD
     */
    public List<Jornada> obtenerTodasLasJornadas() {
        return jornadaRepository.findAll();
    }

    /**
     * --- READ ---
     * @param id El id de la jornada especifica que se busca
     * @return un objeto Jornada que coincida con el ID que se busca
     */
    public Jornada obtenerJornadaPorId(Integer id) {
        return jornadaRepository.findById(id).orElseThrow(() -> new RuntimeException("Jornada no encontrada"));
    }

    /**
     * --- READ ---
     * @param usuarioId Id del usuario que tiene esta jornada x.
     * @return una lista de las jornadas que tenga ese usuario guardadas.
     */
    public List<Jornada> obtenerJornadasDeUsusario(Integer usuarioId) {
        return jornadaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * --- UPDATE ---
     * @param id Id de la jornada que buscamos actualizar.
     * @param jornada Nuevos datos que se actualizaran.
     * @return la jornada con el mismo id que se introdujo, pero con los cambios introducidos.
     */
    public Jornada actualizarJornada(Integer id, Jornada jornada){
        Jornada existente = jornadaRepository.findById(id).orElseThrow(() -> new RuntimeException("Jornada no encontrada"));
        existente.setType(jornada.getType());
        existente.setHora_inicio(jornada.getHora_inicio());
        existente.setHora_fin(jornada.getHora_fin());

        return jornadaRepository.save(existente);
    }

    /**
     * --- DELETE ---
     * @param id Id de la jornada que buscamos borrar
     */
    public void eliminarJornada(Integer id) {
        Jornada existe = jornadaRepository.findById(id).orElseThrow(() -> new RuntimeException("Jornada no encontrada"));
        jornadaRepository.delete(existe);
    }
}
