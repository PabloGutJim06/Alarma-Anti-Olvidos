package com.esail.serverAlarma.dto;

import java.util.List;

public class UsuarioResponseDTO {
    private Integer id;
    private String username;
    private boolean type;
    private List<JornadaResponseDTO> jornadas; // ← cambiado

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Integer id, String username, boolean type,
                              List<JornadaResponseDTO> jornadas) { // ← cambiado
        this.id = id;
        this.username = username;
        this.type = type;
        this.jornadas = jornadas;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isType() { return type; }
    public void setType(boolean type) { this.type = type; }

    public List<JornadaResponseDTO> getJornadas() { return jornadas; } // ← cambiado
    public void setJornadas(List<JornadaResponseDTO> jornadas) { this.jornadas = jornadas; } // ← cambiado
}