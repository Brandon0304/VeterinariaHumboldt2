package com.tuorg.veterinaria.common.exception;

/**
 * Excepción personalizada para errores de negocio.
 *
 * Esta excepción se utiliza para lanzar errores relacionados con
 * reglas de negocio, validaciones de dominio o lógica de aplicación.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Código de error de negocio (opcional).
     */
    private final String errorCode;

    /**
     * Constructor con mensaje.
     *
     * @param message Mensaje descriptivo del error
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
    }

    /**
     * Constructor con mensaje y código de error.
     *
     * @param message Mensaje descriptivo del error
     * @param errorCode Código de error de negocio
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje descriptivo del error
     * @param cause Causa de la excepción
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    /**
     * Constructor completo.
     *
     * @param message Mensaje descriptivo del error
     * @param errorCode Código de error de negocio
     * @param cause Causa de la excepción
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // Getter

    public String getErrorCode() {
        return errorCode;
    }
}

