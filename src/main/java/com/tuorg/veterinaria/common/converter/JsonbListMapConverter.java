package com.tuorg.veterinaria.common.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuorg.veterinaria.common.exception.BusinessException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Convertidor personalizado para campos JSONB que almacenan List<Map<String, Object>>.
 * 
 * Este convertidor normaliza automáticamente los datos al leerlos de la base de datos,
 * convirtiendo objetos JSON simples en arrays cuando sea necesario.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Converter
public class JsonbListMapConverter implements AttributeConverter<List<Map<String, Object>>, String> {

    private static final Logger logger = LoggerFactory.getLogger(JsonbListMapConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = 
            new TypeReference<List<Map<String, Object>>>() {};

    @Override
    public String convertToDatabaseColumn(List<Map<String, Object>> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            logger.error("Error al convertir List<Map<String, Object>> a JSON", e);
            throw new BusinessException("Error al serializar datos JSONB", e);
        }
    }

    @Override
    public List<Map<String, Object>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            // Intentar deserializar como lista primero
            try {
                return objectMapper.readValue(dbData, LIST_MAP_TYPE);
            } catch (Exception e) {
                // Si falla, intentar como objeto simple y convertirlo a lista
                logger.warn("Datos JSONB en formato de objeto simple detectados, normalizando a lista: {}", dbData);
                Map<String, Object> singleObject = objectMapper.readValue(dbData, 
                        new TypeReference<Map<String, Object>>() {});
                List<Map<String, Object>> normalizedList = new ArrayList<>();
                normalizedList.add(singleObject);
                return normalizedList;
            }
        } catch (Exception e) {
            logger.error("Error al deserializar JSONB a List<Map<String, Object>>: {}", dbData, e);
            // Retornar lista vacía en lugar de lanzar excepción para evitar fallos en la aplicación
            return new ArrayList<>();
        }
    }
}

