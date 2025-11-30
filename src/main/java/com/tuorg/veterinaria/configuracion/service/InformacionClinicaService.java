package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.InformacionClinica;
import com.tuorg.veterinaria.configuracion.repository.InformacionClinicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de información de la clínica.
 * Implementa patrón Singleton para garantizar una única instancia activa.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InformacionClinicaService {

    private final InformacionClinicaRepository informacionClinicaRepository;

    /**
     * Obtiene la información activa de la clínica (Singleton).
     * Resultado cacheado para optimizar rendimiento.
     */
    @Cacheable(value = "informacionClinica", key = "'activa'")
    @Transactional(readOnly = true)
    public InformacionClinica obtenerInformacionActiva() {
        log.debug("Obteniendo información activa de la clínica");
        return informacionClinicaRepository.findActivaClinica()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe información activa de la clínica. Debe configurar los datos iniciales."
                ));
    }

    /**
     * Actualiza la información de la clínica.
     * Valida que solo exista una instancia activa (Singleton).
     */
    @CacheEvict(value = "informacionClinica", allEntries = true)
    @Transactional
    public InformacionClinica actualizarInformacion(InformacionClinica informacion) {
        log.info("Actualizando información de la clínica: {}", informacion.getNombreClinica());
        
        // Validar patrón Singleton
        if (informacion.getActivo() && informacionClinicaRepository.existsByActivoTrue()) {
            InformacionClinica actual = informacionClinicaRepository.findActivaClinica()
                    .orElseThrow();
            
            if (!actual.getId().equals(informacion.getId())) {
                throw new IllegalStateException(
                        "Ya existe una clínica activa. Solo puede haber una configuración activa."
                );
            }
        }
        
        InformacionClinica actualizada = informacionClinicaRepository.save(informacion);
        log.info("Información de clínica actualizada exitosamente");
        return actualizada;
    }

    /**
     * Verifica si existe información de clínica configurada.
     */
    @Transactional(readOnly = true)
    public boolean existeConfiguracion() {
        return informacionClinicaRepository.existsByActivoTrue();
    }
}
