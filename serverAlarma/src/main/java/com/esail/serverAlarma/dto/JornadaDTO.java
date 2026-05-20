package com.esail.serverAlarma.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entrada: lo que Flutter envía al CREAR o ACTUALIZAR una jornada.
 * El usuarioId viaja en la URL, nunca aquí.
 * Los campos ultimoAviso* y real* son internos — Flutter no los envía jamás.
 */
public class JornadaDTO {

    private LocalDate dia_semana;
    private String type;
    private LocalTime hora_inicio;
    private LocalTime horaAlmuerzo;
    private LocalTime horaVuelta;
    private LocalTime hora_fin;

    public JornadaDTO() {}

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
}