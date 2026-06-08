package main.java.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un voto emitido. Es inmutable una vez creado.
 * Aplica SRP: solo representa el hecho de votar.
 * Aplica Inmutabilidad: una vez creado no puede modificarse.
 */
public class Voto {
    private final String id;
    private final String votanteId;
    private final String opcionId;
    private final LocalDateTime timestamp;
    private final String hash;

    public Voto(String votanteId, String opcionId) {
        if (votanteId == null || votanteId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del votante es obligatorio");
        }
        if (opcionId == null || opcionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la opción es obligatorio");
        }
        
        this.id = UUID.randomUUID().toString();
        this.votanteId = votanteId;
        this.opcionId = opcionId;
        this.timestamp = LocalDateTime.now();
        this.hash = generarHash();
    }

    // Constructor para carga desde persistencia
    public Voto(String id, String votanteId, String opcionId, LocalDateTime timestamp, String hash) {
        this.id = id;
        this.votanteId = votanteId;
        this.opcionId = opcionId;
        this.timestamp = timestamp;
        this.hash = hash;
    }

    private String generarHash() {
        String data = votanteId + opcionId + timestamp.toString();
        return Integer.toHexString(data.hashCode());
    }

    public String getId() { return id; }
    public String getVotanteId() { return votanteId; }
    public String getOpcionId() { return opcionId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getHash() { return hash; }

    /**
     * Verifica la integridad del voto.
     * @return true si el hash coincide y el voto no fue modificado
     */
    public boolean verificarIntegridad() {
        return hash.equals(generarHash());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voto voto = (Voto) o;
        return id.equals(voto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Voto{id='%s', opcion='%s', timestamp=%s}", id, opcionId, timestamp);
    }
}