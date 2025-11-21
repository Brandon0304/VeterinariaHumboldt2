package com.tuorg.veterinaria.config;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Jackson para manejar correctamente los proxys de Hibernate
 * al serializar entidades en las respuestas JSON.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate5JakartaModule hibernateModule() {
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
        module.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        module.enable(Hibernate5JakartaModule.Feature.REPLACE_PERSISTENT_COLLECTIONS);
        return module;
    }
}

