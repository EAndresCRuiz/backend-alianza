package com.alianza.clients.exception;

/**
 * Excepción personalizada para manejar errores durante la exportación de clientes.
 */
public class ExportException extends RuntimeException {

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}