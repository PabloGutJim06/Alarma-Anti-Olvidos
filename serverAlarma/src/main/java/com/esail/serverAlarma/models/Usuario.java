package com.esail.serverAlarma.models;

import jakarta.persistence.*;

@Entity //Representa la Tabla
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean type;

    protected Usuario() {}

    public Usuario(String username, String password, boolean type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isType() {
        return type;
    }
}
