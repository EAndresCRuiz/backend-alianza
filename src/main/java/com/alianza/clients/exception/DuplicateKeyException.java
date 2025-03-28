package com.alianza.clients.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando se intenta crear un recurso con una clave que ya existe.
 * Esta excepción se mapea automáticamente a una respuesta HTTP 409 (Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateKeyException extends RuntimeException {
    
    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje de error
     */
    public DuplicateKeyException(String message) {
        super(message);
    }
}