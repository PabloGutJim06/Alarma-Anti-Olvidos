package com.esail.serverAlarma.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// ~~~ Clase POJO ~~~ //

@Entity //Representa la Tabla
@Table(name = "usuarios")
public class Usuario {

    // --- Atributos --- //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean type;

    @Column(name="device_token", nullable = true)
    private String deviceToken;

    // 'mappedBy' indica que la entidad 'Jornada' es dueña de la relación (campo 'usuario').
    // 'cascade = CascadeType.ALL' significa que si borras al usuario, se borran sus jornadas.
    // 'orphanRemoval = true' borra las jornadas si las sacas de esta lista
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Jornada> jornadas = new ArrayList<>();

    protected Usuario() {}

    public Usuario(String username, String password, boolean type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {return username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getDeviceToken() {return deviceToken;}

    public void setDeviceToken(String deviceToken) {this.deviceToken = deviceToken;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Jornada> getJornadas() {return jornadas;}

    public void setJornadas(List<Jornada> jornadas) {this.jornadas = jornadas;}


    // --- METODO de CONVENIENCIA --- //
    // Métodos de ayuda para mantener sincronizados ambos lados de la relación en memoria.
    public void addJornada(Jornada jornada){
        this.jornadas.add(jornada);
        jornada.setUsuario(this);
    }

    public void removeJornada(Jornada jornada){
        this.jornadas.remove(jornada);
        jornada.setUsuario(null);
    }

}
