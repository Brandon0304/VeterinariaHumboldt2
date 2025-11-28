package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.configuracion.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la gestión de configuración del sistema.
 * 
 * Este controlador expone endpoints para consultar y actualizar
 * parámetros de configuración del sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/configuracion")  // Sin /api porque el context-path ya lo incluye
public class ConfiguracionController {

    /**
     * Servicio de configuración del sistema.
     */
    private final ConfigService configService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param configService Servicio de configuración
     */
    @Autowired
    public ConfiguracionController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Obtiene el valor de un parámetro por su clave.
     * 
     * @param clave Clave del parámetro
     * @return Respuesta con el valor del parámetro
     */
    @GetMapping("/parametros/{clave}")
    public ResponseEntity<ApiResponse<String>> obtenerParametro(@PathVariable String clave) {
        String valor = configService.obtener(clave);
        return ResponseEntity.ok(ApiResponse.success("Parámetro obtenido exitosamente", valor));
    }

    /**
     * Obtiene todos los parámetros del sistema.
     * 
     * @return Respuesta con todos los parámetros
     */
    @GetMapping("/parametros")
    public ResponseEntity<ApiResponse<Map<String, String>>> obtenerTodos() {
        Map<String, String> parametros = configService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.success("Parámetros obtenidos exitosamente", parametros));
    }

    /**
     * Actualiza el valor de un parámetro.
     * 
     * @param clave Clave del parámetro
     * @param requestBody Cuerpo de la petición con el nuevo valor
     * @return Respuesta de confirmación
     */
    @PutMapping("/parametros/{clave}")
    public ResponseEntity<ApiResponse<Void>> actualizarParametro(
            @PathVariable String clave,
            @RequestBody Map<String, String> requestBody) {
        String valor = requestBody.get("valor");
        configService.actualizarValor(clave, valor);
        return ResponseEntity.ok(ApiResponse.success("Parámetro actualizado exitosamente"));
    }

    /**
     * Recarga todos los parámetros desde la base de datos.
     * 
     * @return Respuesta de confirmación
     */
    @PostMapping("/parametros/recargar")
    public ResponseEntity<ApiResponse<Void>> recargarParametros() {
        configService.cargarTodos();
        return ResponseEntity.ok(ApiResponse.success("Parámetros recargados exitosamente"));
    }
}

