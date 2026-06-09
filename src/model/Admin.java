package model;

import java.util.Objects;

/**
 * Representa un administrador del sistema.
 * Aplica SRP: solo gestiona datos y autenticación del admin.
 */
public class Admin extends Persona {
    private String rol;
    private String passwordHash;

    public Admin(String id, String nombreCompleto, String email, String passwordHash, String rol) {
        super(id, nombreCompleto, id, email);
        setRol(rol);
        setPasswordHash(passwordHash);
    }

    public Admin(String id, String nombreCompleto, String email, String passwordHash) {
        this(id, nombreCompleto, email, passwordHash, "ADMIN");
    }

    public void setRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        String trimmed = rol.trim().toUpperCase();
        if (!trimmed.matches("^(ADMIN|SUPER_ADMIN|AUDITOR)$")) {
            throw new IllegalArgumentException("Rol inválido. Opciones: ADMIN, SUPER_ADMIN, AUDITOR");
        }
        this.rol = trimmed;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        this.passwordHash = passwordHash;
    }

    public String getRol() { return rol; }
    public String getPasswordHash() { return passwordHash; }

    /**
     * Verifica si la contraseña en texto plano coincide.
     * @param plainPassword Contraseña sin hashear
     * @return true si coincide
     */
    public boolean checkPassword(String plainPassword) {
        if (plainPassword == null) return false;
        return this.passwordHash.equals(hashPassword(plainPassword));
    }

    private String hashPassword(String plain) {
        // En producción usar BCrypt. Esto es solo para demo.
        return Integer.toHexString(plain.hashCode());
    }

    @Override
    public String toString() {
        return String.format("Admin{id='%s', nombre='%s', rol='%s'}", getId(), getNombreCompleto(), rol);
    }
}