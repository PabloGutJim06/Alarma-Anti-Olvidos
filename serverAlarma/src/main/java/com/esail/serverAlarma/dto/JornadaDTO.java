package com.esail.serverAlarma.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class JornadaDTO {
    private Integer id;
    private LocalDate fecha;
    private String type;
    private LocalTime hora_inicio;
    private LocalTime hora_final;

    public JornadaDTO() {}

    public JornadaDTO(Integer id, LocalDate fecha, String type, LocalTime hora_inicio, LocalTime hora_final) {
        this.id = id;
        this.fecha = fecha;
        this.type = type;
        this.hora_inicio = hora_inicio;
        this.hora_final = hora_final;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalTime getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(LocalTime hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public LocalTime getHora_final() {
        return hora_final;
    }

    public void setHora_final(LocalTime hora_final) {
        this.hora_final = hora_final;
    }
}
