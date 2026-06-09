package model;

/**
 * Candidato que se presenta a la elección.
 * Aplica SRP: representa un candidato con sus atributos específicos.
 * Aplica LSP (Liskov): puede sustituir a Persona y a Opcion sin problemas.
 */
public class Candidato extends Persona implements Opcion {
    private String partido;
    private String biografia;
    private int orden;

    public Candidato(String id, String nombreCompleto, String identificacion, String email,
                     String partido, String biografia, int orden) {
        super(id, nombreCompleto, identificacion, email);
        setPartido(partido);
        setBiografia(biografia);
        setOrden(orden);
    }

    public Candidato(String id, String nombreCompleto, String partido, String biografia, int orden) {
        this(id, nombreCompleto, "", "", partido, biografia, orden);
    }

    public void setPartido(String partido) {
        if (partido == null || partido.trim().isEmpty()) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        if (partido.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del partido no puede exceder 100 caracteres");
        }
        this.partido = partido.trim();
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia == null ? "" : biografia.trim();
        if (this.biografia.length() > 1000) {
            throw new IllegalArgumentException("La biografía no puede exceder 1000 caracteres");
        }
    }

    public void setOrden(int orden) {
        if (orden < 1) {
            throw new IllegalArgumentException("El orden debe ser un número positivo");
        }
        this.orden = orden;
    }

    public String getPartido() { return partido; }
    public String getBiografia() { return biografia; }
    @Override
    public int getOrden() { return orden; }

    // Implementación de Opcion
    @Override
    public String getNombre() { return getNombreCompleto(); }
    @Override
    public String getDescripcion() { return biografia; }

    @Override
    public String toString() {
        return String.format("Candidato{id='%s', nombre='%s', partido='%s'}", 
                            getId(), getNombreCompleto(), partido);
    }
}