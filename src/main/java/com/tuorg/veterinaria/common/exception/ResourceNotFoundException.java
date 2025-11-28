package com.tuorg.veterinaria.common.exception;

/**
 * Excepción personalizada que se lanza cuando un recurso no se encuentra.
 * 
 * Esta excepción se utiliza cuando se intenta acceder a un recurso
 * (entidad, registro, etc.) que no existe en la base de datos.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Nombre del recurso que no se encontró.
     */
    private final String resourceName;

    /**
     * Nombre del campo utilizado para buscar el recurso.
     */
    private final String fieldName;

    /**
     * Valor del campo utilizado para buscar el recurso.
     */
    private final Object fieldValue;

    /**
     * Constructor con parámetros.
     * 
     * @param resourceName Nombre del recurso (ej: "Usuario", "Paciente")
     * @param fieldName Nombre del campo (ej: "id", "username")
     * @param fieldValue Valor del campo
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s : '%s'", 
                resourceName != null ? resourceName : "Recurso",
                fieldName != null ? fieldName : "campo",
                fieldValue != null ? fieldValue : "null"));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor simple con mensaje personalizado.
     * 
     * @param message Mensaje de error
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    // Getters

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}

