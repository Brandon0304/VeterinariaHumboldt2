package com.tuorg.veterinaria.notificaciones.model;

/**
 * Enumeración de tipos de notificaciones en el sistema.
 * 
 * Define los diferentes tipos de notificaciones que pueden ser enviadas
 * a través de los diferentes canales (Email, App).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public enum TipoNotificacion {
    
    /**
     * Notificación de confirmación de cita creada.
     */
    CITA_CONFIRMADA("Cita Confirmada"),
    
    /**
     * Notificación de reprogramación de cita.
     */
    CITA_REPROGRAMADA("Cita Reprogramada"),
    
    /**
     * Notificación de cancelación de cita.
     */
    CITA_CANCELADA("Cita Cancelada"),
    
    /**
     * Notificación de recordatorio de cita próxima.
     */
    RECORDATORIO_CITA("Recordatorio de Cita"),
    
    /**
     * Notificación de resultado de consulta.
     */
    RESULTADO_CONSULTA("Resultado de Consulta"),
    
    /**
     * Notificación de vacunación completada.
     */
    VACUNACION_COMPLETADA("Vacunación Completada"),
    
    /**
     * Notificación de desparasitación completada.
     */
    DESPARASITACION_COMPLETADA("Desparasitación Completada"),
    
    /**
     * Notificación de factura generada.
     */
    FACTURA_GENERADA("Factura Generada"),
    
    /**
     * Notificación general o informativa.
     */
    INFORMACION_GENERAL("Información General"),
    
    /**
     * Notificación de alerta o aviso importante.
     */
    ALERTA("Alerta");
    
    /**
     * Descripción legible del tipo de notificación.
     */
    private final String descripcion;
    
    /**
     * Constructor del enum.
     * 
     * @param descripcion Descripción del tipo de notificación
     */
    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    /**
     * Obtiene la descripción del tipo de notificación.
     * 
     * @return descripción en formato legible
     */
    public String getDescripcion() {
        return descripcion;
    }
}
