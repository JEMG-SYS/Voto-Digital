// repository/Persistible.java
package repository;

/**
 * Interfaz que define el contrato para las entidades que pueden persistirse en archivos CSV.
 */
public interface Persistible<T> {
    
    /**
     * Convierte la entidad a una línea CSV.
     * @return String en formato CSV con los datos de la entidad
     */
    String toCSV();
    
    /**
     * Crea una entidad a partir de una línea CSV.
     * @param csv Línea CSV con los datos de la entidad
     * @return Nueva instancia de la entidad
     */
    T fromCSV(String csv);
}