package com.tuorg.veterinaria.common.dto;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 * 
 * Proporciona información sobre la página actual, total de páginas,
 * total de elementos y el contenido de la página.
 * 
 * @param <T> Tipo de datos en el contenido de la página
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class PageResponse<T> {

    /**
     * Contenido de la página actual.
     */
    private List<T> content;

    /**
     * Número de la página actual (base 0).
     */
    private int pageNumber;

    /**
     * Tamaño de la página (número de elementos por página).
     */
    private int pageSize;

    /**
     * Total de elementos en todas las páginas.
     */
    private long totalElements;

    /**
     * Total de páginas disponibles.
     */
    private int totalPages;

    /**
     * Indica si es la última página.
     */
    private boolean last;

    /**
     * Indica si es la primera página.
     */
    private boolean first;

    /**
     * Indica si está vacía (sin contenido).
     */
    private boolean empty;

    // Constructor por defecto
    public PageResponse() {
    }

    // Constructor con todos los campos
    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements, 
                       int totalPages, boolean last, boolean first, boolean empty) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
        this.empty = empty;
    }

    /**
     * Constructor desde Spring Data Page.
     * 
     * @param page Página de Spring Data
     */
    public PageResponse(org.springframework.data.domain.Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.empty = page.isEmpty();
    }

    // Getters y Setters

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
