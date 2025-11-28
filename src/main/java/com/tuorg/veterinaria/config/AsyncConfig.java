package com.tuorg.veterinaria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración para habilitar procesamiento asíncrono.
 * 
 * Necesario para el patrón Observer con eventos asíncronos.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Configuración por defecto de Spring para @Async
}

