package com.esail.serverAlarma.service;

import com.esail.serverAlarma.models.Jornada;
import com.esail.serverAlarma.models.Registro;
import com.esail.serverAlarma.repo.JornadaRepository;
import com.esail.serverAlarma.repo.RegistroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroService {
    private final RegistroRepository registroRepository;
    private final JornadaRepository jornadaRepository;

    public RegistroService(RegistroRepository registroRepository, JornadaRepository jornadaRepository) {
       this.registroRepository = registroRepository;
       this.jornadaRepository = jornadaRepository;
    }

    /**
     * --- CREATE ---
     * Recibimos el ID de la jornada al que le pertenece el registro y los datos del nuevo registro
     * @param jornadaId ID de la jornada
     * @param registro Objeto JSON del registro
     * @return el objeto jornada que se guarda.
     */
    public Registro crearRegistro(Integer jornadaId ,Registro registro) {
        Jornada jornada = jornadaRepository.findById(jornadaId).orElseThrow(() -> new RuntimeException("Jornada no encontrada"));
        registro.setJornada(jornada);
        return registroRepository.save(registro);
    }

    /**
     * --- READ ---
     * @param id El id del registro especifico que se busca
     * @return un objeto registro que coincida con el ID que se busca
     */
    public Registro obtenerRegistroPorId(Integer id) {
        return registroRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    /**
     * --- READ ---
     * @param jornadaId Id de la jornada que tiene este registro.
     * @return una lista de los registros que tenga esa jornada .
     */
    public List<Registro> obtenerRegistrosJornada(Integer jornadaId) {
        return registroRepository.findByJornadaId(jornadaId);
    }

    /**
     * --- UPDATE ---
     * @param id Id del registro que buscamos actualizar.
     * @param registro Nuevos datos que se actualizaran.
     * @return el registro con el mismo id que se introdujo, pero con los cambios introducidos.
     */
    public Registro actualizarRegistroPorId(Integer id, Registro registro) {
        Registro existe = registroRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        existe.setTitulo(registro.getTitulo());
        existe.setFechaHora(registro.getFechaHora());

        return registroRepository.save(existe);
    }

    /**
     * --- DELETE ---
     * @param id Id del registro que buscamos borrar
     */
    public void eliminarRegistro(Integer id) {
        Registro existe = registroRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        registroRepository.delete(existe);
    }
}
