package com.esail.serverAlarma.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

// --- Clase POJO Jornada Laboral --- //
@Entity
@Table(name = "jornadas")
public class Jornada {
    // --- Atributos --- //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (nullable = false)
    private LocalDate dia_semana;

    @Column(nullable = false)
    private String type;

    @Column (nullable = false)
    private LocalTime hora_inicio;

    @Column (nullable = false)
    private LocalTime hora_fin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected Jornada() {}

    public Jornada(LocalDate dia_semana, String type, LocalTime hora_inicio, LocalTime hora_fin, Usuario usuario) {
        this.dia_semana = dia_semana;
        this.type = type;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.usuario = usuario;
    }

    // -- Getters y Setters -- //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDia_semana() {
        return dia_semana;
    }

    public void setDia_semana(LocalDate dia_semana) {
        this.dia_semana = dia_semana;
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

    public LocalTime getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(LocalTime hora_fin) {
        this.hora_fin = hora_fin;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
