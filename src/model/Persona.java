package model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Clase abstracta base para todas las personas del sistema.
 * Aplica SRP: solo gestiona atributos comunes de una persona.
 * 
 * @author TuNombre
 * @version 2.0
 */
public abstract class Persona {
    protected final String id;
    protected String nombreCompleto;
    protected String identificacion;
    protected String email;
    
    private static final Pattern SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúñÁÉÍÓÚÑ\\s]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Persona(String id, String nombreCompleto, String identificacion, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío");
        }
        this.id = id;
        
        setNombreCompleto(nombreCompleto);
        setIdentificacion(identificacion);
        setEmail(email);
    }

    // Validaciones en setters (Experto en información)
    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        String trimmed = nombreCompleto.trim();
        if (trimmed.length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
        }
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
        if (!SOLO_LETRAS.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios");
        }
        this.nombreCompleto = trimmed;
    }

    public void setIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación es obligatoria");
        }
        String trimmed = identificacion.trim();
        if (!trimmed.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("La identificación solo debe contener números");
        }
        this.identificacion = trimmed;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Formato de email inválido (ejemplo@correo.com)");
        }
        this.email = trimmed;
    }

    // Getters
    public String getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getIdentificacion() { return identificacion; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(id, persona.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', nombre='%s'}", getClass().getSimpleName(), id, nombreCompleto);
    }
}