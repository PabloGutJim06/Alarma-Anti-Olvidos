package com.esail.serverAlarma.exception;

import com.esail.serverAlarma.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// Devuelve JSON automáticamente sin necesidad de @ResponseBody en cada método
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura: jornada no encontrada, usuario no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(), // 404
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Captura: el empleado intenta fichar un tramo ya fichado
    @ExceptionHandler(YaFichadoException.class)
    public ResponseEntity<ErrorResponseDTO> handleYaFichado(YaFichadoException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(), // 409
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Captura: tipo de fichaje desconocido (no es INICIO, ALMUERZO_INICIO...)
    @ExceptionHandler(FichajeInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleFichajeInvalido(FichajeInvalidoException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(), // 400
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Red de seguridad — captura cualquier excepción no prevista
    // Evita que stacktraces internos lleguen al cliente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "Error interno del servidor. Contacta con el administrador."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
