package com.esail.serverAlarma.models;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Plantilla semanal de horario para un usuario.
 * Un usuario puede tener hasta 7 filas (una por día de la semana).
 * De aquí se leen las horas al generar la Jornada diaria.
 */
@Entity
@Table(name = "horarios_plantilla",
        uniqueConstraints = {
                // Un usuario no puede tener dos plantillas para el mismo día
                @UniqueConstraint(columnNames = {"usuario_id", "dia_semana"})
        })
public class HorarioPlantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // DayOfWeek de Java: MONDAY, TUESDAY, WEDNESDAY...
    // Se guarda como String en BD ("MONDAY", "FRIDAY"...)
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_almuerzo", nullable = false)
    private LocalTime horaAlmuerzo;

    @Column(name = "hora_vuelta", nullable = false)
    private LocalTime horaVuelta;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    // Permite desactivar un día sin borrar la fila
    // Útil para festivos recurrentes o días no laborables
    @Column(nullable = false)
    private boolean activo = true;

    public HorarioPlantilla() {}

    // Getters y Setters
    public Integer getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public DayOfWeek getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraAlmuerzo() { return horaAlmuerzo; }
    public void setHoraAlmuerzo(LocalTime horaAlmuerzo) { this.horaAlmuerzo = horaAlmuerzo; }

    public LocalTime getHoraVuelta() { return horaVuelta; }
    public void setHoraVuelta(LocalTime horaVuelta) { this.horaVuelta = horaVuelta; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}