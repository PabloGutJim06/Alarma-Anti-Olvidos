package com.esail.serverAlarma.repo;

import com.esail.serverAlarma.models.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JornadaRepository extends JpaRepository<Jornada, Integer> {
    List<Jornada> findByUsuarioId(Integer usuarioId);
}
