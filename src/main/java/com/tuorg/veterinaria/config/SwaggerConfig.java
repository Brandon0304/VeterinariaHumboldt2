package com.tuorg.veterinaria.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para documentación y testing de la API.
 * 
 * Esta clase configura Swagger UI que permite:
 * - Ver documentación automática de todos los endpoints
 * - Probar endpoints directamente desde el navegador
 * - Ver esquemas de request/response
 * - Autenticarse con JWT para probar endpoints protegidos
 * 
 * Acceso a Swagger UI: http://localhost:8080/swagger-ui.html
 * Acceso a OpenAPI JSON: http://localhost:8080/v3/api-docs
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configuración principal de OpenAPI/Swagger.
     * 
     * Define la información general de la API, incluyendo título, descripción,
     * versión, contacto y configuración de seguridad JWT.
     * 
     * @return OpenAPI configurado
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Clínico Veterinario - API REST")
                        .version("1.0.0")
                        .description("""
                                API REST completa para el Sistema de Gestión de Clínica Veterinaria.
                                
                                Esta API permite gestionar:
                                - Usuarios y autenticación (JWT)
                                - Pacientes (mascotas) e historias clínicas
                                - Citas médicas
                                - Inventario de productos
                                - Servicios prestados y facturación
                                - Notificaciones
                                - Reportes y estadísticas
                                
                                **Autenticación:**
                                1. Primero haz login en /api/auth/login para obtener un token JWT
                                2. Haz clic en el botón "Authorize" (arriba a la derecha)
                                3. Ingresa: Bearer <tu_token_jwt>
                                4. Ahora podrás probar todos los endpoints protegidos
                                
                                **Nota:** Los endpoints de autenticación (/api/auth/**) son públicos
                                y no requieren token.
                                """)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@veterinaria.com"))
                        .license(new License()
                                .name("Uso Académico")
                                .url("https://example.com/license")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}

