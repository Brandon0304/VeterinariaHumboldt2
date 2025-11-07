package com.tuorg.veterinaria.common.constants;

/**
 * Clase de constantes de la aplicación.
 * 
 * Esta clase centraliza todas las constantes utilizadas en la aplicación,
 * incluyendo mensajes, códigos de error, valores por defecto, etc.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public final class AppConstants {

    /**
     * Constructor privado para prevenir instanciación.
     */
    private AppConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no debe instanciarse");
    }

    // ==================== Mensajes de Respuesta ====================

    /**
     * Mensaje de éxito genérico.
     */
    public static final String MSG_SUCCESS = "Operación realizada exitosamente";

    /**
     * Mensaje de error genérico.
     */
    public static final String MSG_ERROR = "Ocurrió un error al procesar la solicitud";

    /**
     * Mensaje de recurso no encontrado.
     */
    public static final String MSG_RESOURCE_NOT_FOUND = "Recurso no encontrado";

    // ==================== Especies de Animales ====================

    /**
     * Especie: Perro.
     */
    public static final String ESPECIE_PERRO = "perro";

    /**
     * Especie: Gato.
     */
    public static final String ESPECIE_GATO = "gato";

    // ==================== Estados de Citas ====================

    /**
     * Estado de cita: Programada.
     */
    public static final String ESTADO_CITA_PROGRAMADA = "PROGRAMADA";

    /**
     * Estado de cita: Realizada.
     */
    public static final String ESTADO_CITA_REALIZADA = "REALIZADA";

    /**
     * Estado de cita: Cancelada.
     */
    public static final String ESTADO_CITA_CANCELADA = "CANCELADA";

    // ==================== Tipos de Movimiento de Inventario ====================

    /**
     * Tipo de movimiento: Entrada (IN).
     */
    public static final String TIPO_MOVIMIENTO_ENTRADA = "IN";

    /**
     * Tipo de movimiento: Salida (OUT).
     */
    public static final String TIPO_MOVIMIENTO_SALIDA = "OUT";

    /**
     * Tipo de movimiento: Ajuste (AJUSTE).
     */
    public static final String TIPO_MOVIMIENTO_AJUSTE = "AJUSTE";

    // ==================== Estados de Factura ====================

    /**
     * Estado de factura: Pendiente.
     */
    public static final String ESTADO_FACTURA_PENDIENTE = "PENDIENTE";

    /**
     * Estado de factura: Pagada.
     */
    public static final String ESTADO_FACTURA_PAGADA = "PAGADA";

    /**
     * Estado de factura: Anulada.
     */
    public static final String ESTADO_FACTURA_ANULADA = "ANULADA";

    // ==================== Estados de Notificación ====================

    /**
     * Estado de notificación: Pendiente.
     */
    public static final String ESTADO_NOTIFICACION_PENDIENTE = "PENDIENTE";

    /**
     * Estado de notificación: Enviada.
     */
    public static final String ESTADO_NOTIFICACION_ENVIADA = "ENVIADA";

    /**
     * Estado de notificación: Fallida.
     */
    public static final String ESTADO_NOTIFICACION_FALLIDA = "FALLIDA";

    // ==================== Canales de Notificación ====================

    /**
     * Canal de notificación: Email.
     */
    public static final String CANAL_EMAIL = "EMAIL";

    /**
     * Canal de notificación: SMS.
     */
    public static final String CANAL_SMS = "SMS";

    /**
     * Canal de notificación: App.
     */
    public static final String CANAL_APP = "APP";

    // ==================== Roles del Sistema ====================

    /**
     * Rol: Administrador.
     */
    public static final String ROL_ADMIN = "ADMIN";

    /**
     * Rol: Veterinario.
     */
    public static final String ROL_VETERINARIO = "VETERINARIO";

    /**
     * Rol: Secretario.
     */
    public static final String ROL_SECRETARIO = "SECRETARIO";

    /**
     * Rol: Cliente.
     */
    public static final String ROL_CLIENTE = "CLIENTE";

    // ==================== Configuración de Archivos ====================

    /**
     * Tamaño máximo de archivo en bytes (10MB).
     */
    public static final long MAX_FILE_SIZE = 10485760L;

    /**
     * Directorio de carga de archivos por defecto.
     */
    public static final String DEFAULT_UPLOAD_DIR = "./uploads";

    // ==================== Validaciones ====================

    /**
     * Longitud mínima de contraseña.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Longitud máxima de nombre de usuario.
     */
    public static final int MAX_USERNAME_LENGTH = 60;

    /**
     * Longitud mínima de nombre de usuario.
     */
    public static final int MIN_USERNAME_LENGTH = 3;
}

