package com.tuorg.veterinaria.common.dto;

import java.time.LocalDateTime;

/**
 * Clase DTO genérica para respuestas de la API.
 * 
 * Esta clase proporciona un formato estándar para todas las respuestas
 * de los endpoints REST, incluyendo el estado, mensaje, datos y timestamp.
 * 
 * @param <T> Tipo de datos que contiene la respuesta
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class ApiResponse<T> {

    /**
     * Indica si la operación fue exitosa.
     */
    private boolean success;

    /**
     * Mensaje descriptivo de la respuesta.
     */
    private String message;

    /**
     * Datos de la respuesta (puede ser null si no hay datos).
     */
    private T data;

    /**
     * Timestamp de cuando se generó la respuesta.
     */
    private LocalDateTime timestamp;

    /**
     * Constructor por defecto.
     * Inicializa el timestamp con la fecha y hora actual.
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor con parámetros.
     * 
     * @param success Indica si la operación fue exitosa
     * @param message Mensaje descriptivo
     * @param data Datos de la respuesta
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Método estático para crear una respuesta exitosa.
     * 
     * @param <T> Tipo de datos
     * @param message Mensaje de éxito
     * @param data Datos de la respuesta
     * @return ApiResponse con success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Método estático para crear una respuesta exitosa sin datos.
     * 
     * @param <T> Tipo de datos
     * @param message Mensaje de éxito
     * @return ApiResponse con success=true y data=null
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Método estático para crear una respuesta de error.
     * 
     * @param <T> Tipo de datos
     * @param message Mensaje de error
     * @return ApiResponse con success=false
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // Getters y Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

