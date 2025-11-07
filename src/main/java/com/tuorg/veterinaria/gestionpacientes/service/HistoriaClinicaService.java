package com.tuorg.veterinaria.gestionpacientes.service;

import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestionpacientes.model.HistoriaClinica;
import com.tuorg.veterinaria.gestionpacientes.model.RegistroMedico;
import com.tuorg.veterinaria.gestionpacientes.repository.HistoriaClinicaRepository;
import com.tuorg.veterinaria.gestionpacientes.repository.RegistroMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de historias clínicas.
 * 
 * Este servicio proporciona métodos para agregar registros médicos
 * a las historias clínicas y exportar información.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class HistoriaClinicaService {

    /**
     * Repositorio de historias clínicas.
     */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
     * Repositorio de registros médicos.
     */
    private final RegistroMedicoRepository registroMedicoRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param historiaClinicaRepository Repositorio de historias clínicas
     * @param registroMedicoRepository Repositorio de registros médicos
     */
    @Autowired
    public HistoriaClinicaService(HistoriaClinicaRepository historiaClinicaRepository,
                                  RegistroMedicoRepository registroMedicoRepository) {
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.registroMedicoRepository = registroMedicoRepository;
    }

    /**
     * Obtiene la historia clínica de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Historia clínica del paciente
     */
    @Transactional(readOnly = true)
    public HistoriaClinica obtenerPorPaciente(Long pacienteId) {
        return historiaClinicaRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "paciente_id", pacienteId));
    }

    /**
     * Agrega un registro médico a una historia clínica.
     * 
     * Esta operación es transaccional y también debe consumir insumos
     * del inventario según lo especificado en el registro.
     * 
     * @param historiaId ID de la historia clínica
     * @param registro Registro médico a agregar
     * @return Registro médico creado
     */
    @Transactional
    public RegistroMedico agregarRegistro(Long historiaId, RegistroMedico registro) {
        HistoriaClinica historia = historiaClinicaRepository.findById(historiaId)
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "id", historiaId));

        registro.setHistoria(historia);
        RegistroMedico registroGuardado = registroMedicoRepository.save(registro);

        // TODO: Consumir insumos del inventario según registro.getInsumosUsados()
        // Esto debe implementarse en el servicio de inventario

        return registroGuardado;
    }

    /**
     * Obtiene todos los registros médicos de una historia clínica.
     * 
     * @param historiaId ID de la historia clínica
     * @return Lista de registros médicos
     */
    @Transactional(readOnly = true)
    public List<RegistroMedico> obtenerRegistros(Long historiaId) {
        HistoriaClinica historia = historiaClinicaRepository.findById(historiaId)
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "id", historiaId));
        return registroMedicoRepository.findByHistoriaId(historiaId);
    }

    /**
     * Exporta la historia clínica como PDF (simplificado).
     * 
     * En una implementación completa, esto generaría un PDF real.
     * 
     * @param historiaId ID de la historia clínica
     * @return Array de bytes representando el PDF
     */
    @Transactional(readOnly = true)
    public byte[] exportarPDF(Long historiaId) {
        HistoriaClinica historia = historiaClinicaRepository.findById(historiaId)
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "id", historiaId));

        // TODO: Implementar generación real de PDF con iText o JasperReports
        // Por ahora retornamos un array vacío
        return new byte[0];
    }
}
