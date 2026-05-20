package com.esail.serverAlarma.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// --- Clase POJO Jornada Laboral --- //
@Entity
@Table(name = "jornadas", indexes = {
        @Index(name = "idx_jornada_dia", columnList = "dia_semana")
})
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

    @Column(name = "hora_almuerzo", nullable = true)
    private LocalTime horaAlmuerzo;

    @Column(name = "hora_vuelta", nullable = true)
    private LocalTime horaVuelta;

    @Column(name = "real_inicio")
    private LocalTime realInicio;

    @Column(name = "real_almuerzo_inicio")
    private LocalTime realAlmuerzoInicio;

    @Column(name = "real_almuerzo_fin")
    private LocalTime realAlmuerzoFin;

    @Column(name = "real_fin")
    private LocalTime realFin;

    @Column(name = "ultimo_aviso_inicio")
    private LocalDateTime ultimoAvisoInicio;

    @Column(name = "ultimo_aviso_almuerzo_inicio")
    private LocalDateTime ultimoAvisoAlmuerzoInicio;

    @Column(name = "ultimo_aviso_almuerzo_fin")
    private LocalDateTime ultimoAvisoAlmuerzoFin;

    @Column(name = "ultimo_aviso_fin")
    private LocalDateTime ultimoAvisoFin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Registro> registros =  new ArrayList<>();

    public Jornada() {}

    public Jornada(LocalDate dia_semana, String type, LocalTime hora_inicio,
                   LocalTime horaAlmuerzo, LocalTime horaVuelta, LocalTime hora_fin, Usuario usuario) {
        this.dia_semana = dia_semana;
        this.type = type;
        this.hora_inicio = hora_inicio;
        this.horaAlmuerzo = horaAlmuerzo;
        this.horaVuelta = horaVuelta;
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

    public List<Registro> getRegistros() {return registros;}

    public void setRegistros(List<Registro> registros) {this.registros = registros;}

    public LocalTime getHoraAlmuerzo() { return horaAlmuerzo; }
    public void setHoraAlmuerzo(LocalTime horaAlmuerzo) { this.horaAlmuerzo = horaAlmuerzo; }

    public LocalTime getHoraVuelta() { return horaVuelta; }
    public void setHoraVuelta(LocalTime horaVuelta) { this.horaVuelta = horaVuelta; }

    public LocalTime getRealInicio() {
        return realInicio;
    }

    public void setRealInicio(LocalTime realInicio) {
        this.realInicio = realInicio;
    }

    public LocalTime getRealAlmuerzoInicio() {
        return realAlmuerzoInicio;
    }

    public void setRealAlmuerzoInicio(LocalTime realAlmuerzoInicio) {
        this.realAlmuerzoInicio = realAlmuerzoInicio;
    }

    public LocalTime getRealAlmuerzoFin() {
        return realAlmuerzoFin;
    }

    public void setRealAlmuerzoFin(LocalTime realAlmuerzoFin) {
        this.realAlmuerzoFin = realAlmuerzoFin;
    }

    public LocalTime getRealFin() {
        return realFin;
    }

    public void setRealFin(LocalTime realFin) {
        this.realFin = realFin;
    }

    public LocalDateTime getUltimoAvisoInicio() { return ultimoAvisoInicio; }
    public void setUltimoAvisoInicio(LocalDateTime t) { this.ultimoAvisoInicio = t; }

    public LocalDateTime getUltimoAvisoAlmuerzoInicio() { return ultimoAvisoAlmuerzoInicio; }
    public void setUltimoAvisoAlmuerzoInicio(LocalDateTime t) { this.ultimoAvisoAlmuerzoInicio = t; }

    public LocalDateTime getUltimoAvisoAlmuerzoFin() { return ultimoAvisoAlmuerzoFin; }
    public void setUltimoAvisoAlmuerzoFin(LocalDateTime t) { this.ultimoAvisoAlmuerzoFin = t; }

    public LocalDateTime getUltimoAvisoFin() { return ultimoAvisoFin; }
    public void setUltimoAvisoFin(LocalDateTime t) { this.ultimoAvisoFin = t; }

    // --- METODO DE CONVENIENCIA --- //
    public void addRegistro(Registro registro){
        this.registros.add(registro);
        registro.setJornada(this);
    }

    public void  removeRegistro(Registro registro){
        this.registros.remove(registro);
        registro.setJornada(null);
    }
}
