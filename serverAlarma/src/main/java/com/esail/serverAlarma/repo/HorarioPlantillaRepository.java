package com.esail.serverAlarma.repo;

import com.esail.serverAlarma.models.HorarioPlantilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface HorarioPlantillaRepository extends JpaRepository<HorarioPlantilla, Integer> {

    // Todas las plantillas activas de un usuario
    List<HorarioPlantilla> findByUsuarioIdAndActivoTrue(Integer usuarioId);

    // La plantilla de un usuario para un día concreto
    Optional<HorarioPlantilla> findByUsuarioIdAndDiaSemana(Integer usuarioId, DayOfWeek diaSemana);

    // Todas las plantillas activas de hoy
    @Query("SELECT h FROM HorarioPlantilla h JOIN FETCH h.usuario WHERE h.diaSemana = :dia AND h.activo = true")
    List<HorarioPlantilla> findByDiaSemanaAndActivoTrue(@Param("dia") DayOfWeek dia);
}