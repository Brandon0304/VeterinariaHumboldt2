package com.tuorg.veterinaria.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuración para habilitar la auditoría JPA.
 * 
 * Esta configuración activa las anotaciones @CreatedBy, @CreatedDate,
 * @LastModifiedBy y @LastModifiedDate en las entidades.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingConfig {
    // La configuración se maneja mediante anotaciones
}
