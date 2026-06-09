package model;

import java.util.Objects;

/**
 * Opción de votación simple (texto + descripción).
 * Aplica SRP: solo representa una opción no-persona.
 */
public class OpcionSimple implements Opcion {
    private final String id;
    private String nombre;
    private String descripcion;
    private int orden;

    public OpcionSimple(String id, String nombre, String descripcion, int orden) {
        this.id = Objects.requireNonNull(id, "El ID no puede ser nulo");
        setNombre(nombre);
        setDescripcion(descripcion);
        setOrden(orden);
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la opción es obligatorio");
        }
        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
        this.nombre = nombre.trim();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        if (this.descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }
    }

    public void setOrden(int orden) {
        if (orden < 1) {
            throw new IllegalArgumentException("El orden debe ser un número positivo");
        }
        this.orden = orden;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getNombre() { return nombre; }
    @Override
    public String getDescripcion() { return descripcion; }
    @Override
    public int getOrden() { return orden; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpcionSimple that = (OpcionSimple) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("OpcionSimple{id='%s', nombre='%s', orden=%d}", id, nombre, orden);
    }
}