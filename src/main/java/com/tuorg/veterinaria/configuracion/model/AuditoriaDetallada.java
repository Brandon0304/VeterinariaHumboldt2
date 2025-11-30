package com.tuorg.veterinaria.configuracion.model;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa la auditoría detallada del sistema.
 * 
 * Implementa patrón Memento: almacena el estado anterior y posterior de cada operación,
 * permitiendo análisis forense y potencial reversión de cambios.
 * 
 * Registra TODAS las operaciones críticas: crear, editar, eliminar, exportar, aprobar.
 * 
 * NOTA: Esta entidad NO extiende Auditable para evitar auditoría circular.
 */
@Entity
@Table(name = "auditoria_detallada", schema = "public",
    indexes = {
        @Index(name = "idx_auditoria_fecha", columnList = "fecha_accion"),
        @Index(name = "idx_auditoria_usuario", columnList = "usuario_id"),
        @Index(name = "idx_auditoria_entidad", columnList = "entidad,entidad_id")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDetallada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Referencia al historial de acciones.
     */
    @Column(name = "historial_accion_id")
    private Long historialAccionId;

    /**
     * Usuario que realizó la acción.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Nombre del rol del usuario para búsqueda rápida.
     */
    @Column(name = "rol_nombre", nullable = false, length = 50)
    private String rolNombre;

    /**
     * Módulo del sistema afectado.
     */
    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo;

    /**
     * Nombre de la entidad afectada.
     * Ejemplos: Usuario, Paciente, Cita, HistoriaClinica, Factura
     */
    @Column(name = "entidad", length = 100)
    private String entidad;

    /**
     * ID del registro afectado.
     */
    @Column(name = "entidad_id")
    private Long entidadId;

    /**
     * Estado anterior del registro en formato JSON (patrón Memento).
     * Permite reversión de cambios y auditoría forense.
     */
    @Column(name = "datos_anteriores", columnDefinition = "JSONB")
    private String datosAnteriores;

    /**
     * Estado posterior del registro en formato JSON.
     */
    @Column(name = "datos_nuevos", columnDefinition = "JSONB")
    private String datosNuevos;

    /**
     * Nivel de relevancia de la acción.
     * Valores: ALTA, NORMAL, BAJA
     */
    @Column(name = "relevancia", length = 20)
    private String relevancia;

    /**
     * Indica si la acción requiere revisión manual.
     */
    @Column(name = "requiere_revision")
    private Boolean requiereRevision;

    /**
     * Dirección IP desde donde se realizó la acción.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent del navegador.
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Fecha en que se realizó la acción.
     */
    @Column(name = "fecha_accion")
    private LocalDateTime fechaAccion;

    /**
     * Tipo de operación realizada.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacion", length = 30)
    private TipoOperacion tipoOperacion;

    /**
     * Enumeración que representa los tipos de operaciones auditables.
     */
    public enum TipoOperacion {
        CREAR,
        EDITAR,
        ELIMINAR,
        EXPORTAR,
        APROBAR,
        RECHAZAR,
        CONSULTAR
    }

}
