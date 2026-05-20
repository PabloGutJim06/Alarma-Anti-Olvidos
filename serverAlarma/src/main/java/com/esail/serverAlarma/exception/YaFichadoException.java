package com.esail.serverAlarma.exception;

public class YaFichadoException extends RuntimeException {

    public YaFichadoException(String tipo) {
        super("El fichaje '" + tipo + "' ya fue registrado para esta jornada");
    }
}
