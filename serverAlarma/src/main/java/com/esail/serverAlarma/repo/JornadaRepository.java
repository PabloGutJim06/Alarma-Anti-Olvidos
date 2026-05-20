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

    // Buscamos todas las jornadas de hoy donde falte algún fichaje y la hora prevista ya haya pasado
    @Query("SELECT j FROM Jornada j " +
            "JOIN FETCH j.usuario " +        // ← carga el Usuario en el mismo SELECT
            "WHERE j.dia_semana = :hoy " +
            "AND ((j.realInicio IS NULL AND j.hora_inicio < :ahora) " +
            "OR (j.realAlmuerzoInicio IS NULL AND j.horaAlmuerzo < :ahora) " +
            "OR (j.realAlmuerzoFin IS NULL AND j.horaVuelta < :ahora) " +
            "OR (j.realFin IS NULL AND j.hora_fin < :ahora))")
    List<Jornada> findCandidatosOlvido(@Param("hoy") LocalDate hoy, @Param("ahora") LocalTime ahora);
}

