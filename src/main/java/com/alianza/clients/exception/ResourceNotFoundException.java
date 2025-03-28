package com.alianza.clients.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando no se encuentra un recurso solicitado.
 * Esta excepción se mapea automáticamente a una respuesta HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje de error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor que genera un mensaje descriptivo basado en el recurso y el campo.
     *
     * @param resourceName Nombre del recurso (ej. "Cliente")
     * @param fieldName Nombre del campo (ej. "id")
     * @param fieldValue Valor del campo que no se encontró
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
    }
}