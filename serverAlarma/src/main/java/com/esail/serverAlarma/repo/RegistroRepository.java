package com.esail.serverAlarma.repo;

import com.esail.serverAlarma.models.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Integer> {
    List<Registro> findByJornadaId(Integer jornadaId);
    boolean existsByJornadaIdAndTituloContainingIgnoreCase(Integer jornadaId, String titulo);
}
