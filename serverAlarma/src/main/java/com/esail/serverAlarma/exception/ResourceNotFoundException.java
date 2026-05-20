package com.esail.serverAlarma.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Integer id) {
        // Mensaje claro y consistente: "Jornada no encontrada con id: 99"
        super(recurso + " no encontrada con id: " + id);
    }
}
