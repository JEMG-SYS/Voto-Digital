package main.java.model;

/**
 * Contrato para cualquier elemento votable.
 * Aplica ISP: interfaz pequeña y específica.
 * Aplica Polimorfismo (GRASP): permite tratar OpcionSimple y Candidato de forma uniforme.
 */
public interface Opcion {
    String getId();
    String getNombre();
    String getDescripcion();
    int getOrden();
}