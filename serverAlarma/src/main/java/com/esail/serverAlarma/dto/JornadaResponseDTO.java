package com.esail.serverAlarma.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Salida: lo que Flutter recibe en CUALQUIER endpoint de jornada.
 * Nunca exponemos la entidad directamente.
 * Los campos ultimoAviso* son internos del servidor — no salen nunca.
 */
public class JornadaResponseDTO {

    private Integer id;
    private LocalDate dia_semana;
    private String type;

    // Horas previstas
    private LocalTime hora_inicio;
    private LocalTime horaAlmuerzo;
    private LocalTime horaVuelta;
    private LocalTime hora_fin;

    // Fichajes reales — null si aún no se han registrado.
    // Flutter los usa para habilitar/deshabilitar botones.
    private LocalTime realInicio;
    private LocalTime realAlmuerzoInicio;
    private LocalTime realAlmuerzoFin;
    private LocalTime realFin;

    public JornadaResponseDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDia_semana() { return dia_semana; }
    public void setDia_semana(LocalDate dia_semana) { this.dia_semana = dia_semana; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalTime getHora_inicio() { return hora_inicio; }
    public void setHora_inicio(LocalTime hora_inicio) { this.hora_inicio = hora_inicio; }

    public LocalTime getHoraAlmuerzo() { return horaAlmuerzo; }
    public void setHoraAlmuerzo(LocalTime horaAlmuerzo) { this.horaAlmuerzo = horaAlmuerzo; }

    public LocalTime getHoraVuelta() { return horaVuelta; }
    public void setHoraVuelta(LocalTime horaVuelta) { this.horaVuelta = horaVuelta; }

    public LocalTime getHora_fin() { return hora_fin; }
    public void setHora_fin(LocalTime hora_fin) { this.hora_fin = hora_fin; }

    public LocalTime getRealInicio() { return realInicio; }
    public void setRealInicio(LocalTime realInicio) { this.realInicio = realInicio; }

    public LocalTime getRealAlmuerzoInicio() { return realAlmuerzoInicio; }
    public void setRealAlmuerzoInicio(LocalTime v) { this.realAlmuerzoInicio = v; }

    public LocalTime getRealAlmuerzoFin() { return realAlmuerzoFin; }
    public void setRealAlmuerzoFin(LocalTime v) { this.realAlmuerzoFin = v; }

    public LocalTime getRealFin() { return realFin; }
    public void setRealFin(LocalTime realFin) { this.realFin = realFin; }
}