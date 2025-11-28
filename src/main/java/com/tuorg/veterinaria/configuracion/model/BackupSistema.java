package com.tuorg.veterinaria.configuracion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un backup del sistema.
 * 
 * Esta clase almacena información sobre los backups realizados
 * del sistema, incluyendo la ruta del archivo y metadatos adicionales.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "backups_sistema", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackupSistema {

    /**
     * Identificador único del backup (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_backup")
    private Long idBackup;

    /**
     * Fecha y hora de creación del backup.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Ruta del archivo de backup en el sistema de archivos.
     */
    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    /**
     * Metadatos adicionales del backup en formato JSON.
     * Puede incluir información como tamaño, tipo, etc.
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;
}

