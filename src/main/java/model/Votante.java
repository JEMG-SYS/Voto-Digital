package main.java.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Representa un votante registrado en el sistema.
 * Aplica SRP: solo gestiona datos y comportamientos del votante.
 * Aplica Experto en información: sabe si ya votó y su edad.
 */
public class Votante extends Persona {
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String genero;
    private boolean haVotado;
    private final LocalDateTime fechaRegistro;
    
    private static final String[] GENEROS_VALIDOS = {"MASCULINO", "FEMENINO", "OTRO", "PREFIERO_NO_DECIR"};

    public Votante(String id, String nombreCompleto, String identificacion, String email,
                   LocalDate fechaNacimiento, String direccion, String telefono, String genero) {
        super(id, nombreCompleto, identificacion, email);
        setFechaNacimiento(fechaNacimiento);
        setDireccion(direccion);
        setTelefono(telefono);
        setGenero(genero);
        this.haVotado = false;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Constructor con fecha en String (para facilitar desde UI)
    public Votante(String id, String nombreCompleto, String identificacion, String email,
                   String fechaNacimientoStr, String direccion, String telefono, String genero) {
        this(id, nombreCompleto, identificacion, email, 
             parseFecha(fechaNacimientoStr), direccion, telefono, genero);
    }

    // Constructor mínimo
    public Votante(String id, String nombreCompleto, String identificacion, String email) {
        this(id, nombreCompleto, identificacion, email, (LocalDate) null, "", "", "");
    }

    // Validaciones en setters
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        LocalDate hoy = LocalDate.now();
        int edad = Period.between(fechaNacimiento, hoy).getYears();
        if (edad < 16) {
            throw new IllegalArgumentException("Debe ser mayor de 16 años para votar");
        }
        if (edad > 120) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion == null ? "" : direccion.trim();
    }

    public void setTelefono(String telefono) {
        if (telefono != null && !telefono.trim().isEmpty()) {
            String trimmed = telefono.trim();
            if (!trimmed.matches("^[0-9]{7,15}$")) {
                throw new IllegalArgumentException("El teléfono debe contener entre 7 y 15 dígitos");
            }
            this.telefono = trimmed;
        } else {
            this.telefono = "";
        }
    }

    public void setGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            this.genero = "PREFIERO_NO_DECIR";
            return;
        }
        String trimmed = genero.trim().toUpperCase();
        boolean valido = false;
        for (String g : GENEROS_VALIDOS) {
            if (g.equals(trimmed)) {
                valido = true;
                break;
            }
        }
        if (!valido) {
            throw new IllegalArgumentException("Género no válido. Opciones: MASCULINO, FEMENINO, OTRO, PREFIERO_NO_DECIR");
        }
        this.genero = trimmed;
    }

    // Getters
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getGenero() { return genero; }
    public boolean isHaVotado() { return haVotado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    /**
     * Marca al votante como que ya emitió su voto.
     * Aplica Experto en información: el votante sabe si ya votó.
     * @throws IllegalStateException si ya había votado
     */
    public void registrarVoto() {
        if (haVotado) {
            throw new IllegalStateException("Este votante ya ha emitido su voto");
        }
        this.haVotado = true;
    }

    /**
     * Calcula la edad actual del votante.
     */
    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private static LocalDate parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(fechaStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use dd/MM/yyyy");
        }
    }

    @Override
    public String toString() {
        return String.format("Votante{id='%s', nombre='%s', haVotado=%s}", 
                            getId(), getNombreCompleto(), haVotado);
    }
}