package com.esail.serverAlarma.exception;

// 400 Bad Request — el cliente mandó un tipo que no existe
public class FichajeInvalidoException extends RuntimeException {

    public FichajeInvalidoException(String tipo) {
        super("Tipo de fichaje no válido: '" + tipo + "'. " +
                "Valores aceptados: INICIO, ALMUERZO_INICIO, ALMUERZO_FIN, FIN");
    }
}
