package main.java.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Representa una votación: su estado, fechas y opciones disponibles.
 * Aplica SRP: solo gestiona el proceso de votación.
 * Aplica Experto en información: sabe si permite votar y cómo gestionar sus opciones.
 */
public class Votacion {
    private final String id;
    private String titulo;
    private boolean activa;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<Opcion> opciones;

    public Votacion(String id, String titulo) {
        this.id = id;
        setTitulo(titulo);
        this.activa = false;
        this.opciones = new ArrayList<>();
    }

    public Votacion(String titulo) {
        this(UUID.randomUUID().toString(), titulo);
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la votación es obligatorio");
        }
        if (titulo.trim().length() > 200) {
            throw new IllegalArgumentException("El título no puede exceder 200 caracteres");
        }
        this.titulo = titulo.trim();
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isActiva() { return activa; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public List<Opcion> getOpciones() { return Collections.unmodifiableList(opciones); }

    /**
     * Determina si actualmente se puede votar.
     * Aplica Experto en información: la votación conoce sus fechas y estado.
     */
    public boolean permiteVotar() {
        if (!activa) return false;
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaInicio != null && ahora.isBefore(fechaInicio)) return false;
        if (fechaFin != null && ahora.isAfter(fechaFin)) return false;
        return true;
    }

    /**
     * Valida que la votación esté activa y en curso.
     * @throws IllegalStateException si no se puede votar
     */
    public void validarVotacionActiva() {
        if (!activa) {
            throw new IllegalStateException("La votación no está activa");
        }
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaInicio != null && ahora.isBefore(fechaInicio)) {
            throw new IllegalStateException(String.format("La votación iniciará el %s", fechaInicio));
        }
        if (fechaFin != null && ahora.isAfter(fechaFin)) {
            throw new IllegalStateException(String.format("La votación finalizó el %s", fechaFin));
        }
    }

    /**
     * Inicia la votación.
     * @param fechaFin Fecha límite (puede ser null para indefinida)
     */
    public void iniciar(LocalDateTime fechaFin) {
        if (activa) {
            throw new IllegalStateException("La votación ya está activa");
        }
        if (fechaFin != null && fechaFin.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de fin debe ser futura");
        }
        this.activa = true;
        this.fechaInicio = LocalDateTime.now();
        this.fechaFin = fechaFin;
    }

    /**
     * Finaliza la votación inmediatamente.
     */
    public void finalizar() {
        if (!activa) {
            throw new IllegalStateException("La votación no está activa");
        }
        this.activa = false;
    }

    /**
     * Extiende la fecha de finalización.
     */
    public void extender(LocalDateTime nuevaFechaFin) {
        if (!activa) {
            throw new IllegalStateException("No se puede extender una votación inactiva");
        }
        if (nuevaFechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin no puede ser nula");
        }
        if (nuevaFechaFin.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La nueva fecha debe ser futura");
        }
        this.fechaFin = nuevaFechaFin;
    }

    /**
     * Agrega una opción y las ordena por su campo 'orden'.
     */
    public void agregarOpcion(Opcion opcion) {
        if (opcion == null) {
            throw new IllegalArgumentException("La opción no puede ser nula");
        }
        if (opciones.stream().anyMatch(o -> o.getId().equals(opcion.getId()))) {
            throw new IllegalArgumentException("Ya existe una opción con ese ID");
        }
        opciones.add(opcion);
        opciones.sort((a, b) -> Integer.compare(a.getOrden(), b.getOrden()));
    }

    public void eliminarOpcion(String opcionId) {
        if (opcionId == null || opcionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la opción es obligatorio");
        }
        opciones.removeIf(op -> op.getId().equals(opcionId));
    }

    /**
     * Busca una opción por su ID.
     * @return Opcion encontrada o null
     */
    public Opcion buscarOpcion(String opcionId) {
        return opciones.stream()
                .filter(op -> op.getId().equals(opcionId))
                .findFirst()
                .orElse(null);
    }

    public int getTotalOpciones() {
        return opciones.size();
    }

    @Override
    public String toString() {
        return String.format("Votacion{id='%s', titulo='%s', activa=%s, fin=%s, opciones=%d}", 
                            id, titulo, activa, fechaFin, opciones.size());
    }
}