package com.esail.serverAlarma.repo;

import com.esail.serverAlarma.models.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface JornadaRepository extends JpaRepository<Jornada, Integer> {
    List<Jornada> findByUsuarioId(Integer usuarioId);
    @Query("SELECT j FROM Jornada j WHERE j.dia_semana = :dia AND j.hora_inicio = :hora")
    List<Jornada> findByDiaSemanaAndHoraInicio(@Param("dia") LocalDate dia, @Param("hora") LocalTime hora);
}

