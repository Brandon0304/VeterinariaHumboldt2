package com.tuorg.veterinaria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración para habilitar procesamiento asíncrono.
 * 
 * Necesario para el patrón Observer con eventos asíncronos.
 * Configura un executor específico para el envío de emails.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Executor específico para tareas de envío de emails.
     * Evita que las notificaciones bloqueen otros procesos asíncronos.
     */
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("email-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

}

