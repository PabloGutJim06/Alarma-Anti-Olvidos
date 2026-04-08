package com.esail.serverAlarma.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros")
public class Registro {
    // --- Atributos --- //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id")
    private Jornada jornada;

    protected Registro() {}

    public Registro(String titulo, LocalDateTime fechaHora) {
        this.titulo = titulo;
        this.fechaHora = fechaHora;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }
}
