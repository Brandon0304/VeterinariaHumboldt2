package com.tuorg.veterinaria.gestionpacientes.model;

import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Entidad que representa un registro médico dentro de una historia clínica.
 * 
 * Cada registro médico documenta una visita, consulta o procedimiento
 * realizado al paciente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "registros_medicos", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroMedico {

    /**
     * Identificador único del registro médico (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long idRegistro;

    /**
     * Historia clínica a la que pertenece este registro.
     * Relación Many-to-One con la entidad HistoriaClinica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaClinica historia;

    /**
     * Fecha y hora del registro médico.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    /**
     * Motivo de la consulta o visita.
     */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /**
     * Diagnóstico realizado.
     */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    /**
     * Signos vitales registrados en formato JSON.
     * Ejemplo: {"temperatura": 38.5, "frecuencia_cardiaca": 120, ...}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "signos_vitales", columnDefinition = "JSONB")
    private Map<String, Object> signosVitales;

    /**
     * Tratamiento prescrito.
     */
    @Column(name = "tratamiento", columnDefinition = "TEXT")
    private String tratamiento;

    /**
     * Veterinario que realizó el registro.
     * Relación Many-to-One con la entidad UsuarioVeterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id")
    private UsuarioVeterinario veterinario;

    /**
     * Insumos utilizados durante la consulta en formato JSON.
     * Ejemplo: [{"productoId": 1, "cantidad": 2}, ...]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "insumos_usados", columnDefinition = "JSONB")
    private List<Map<String, Object>> insumosUsados;

    /**
     * Archivos adjuntos (URLs o referencias) en formato JSON.
     * Ejemplo: ["url1", "url2", ...]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "archivos", columnDefinition = "JSONB")
    private List<String> archivos;
}

