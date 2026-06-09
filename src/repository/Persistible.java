package repository;

public interface Persistible<T> {
    String toCSV(T entidad);   // Recibe la entidad a serializar
    T fromCSV(String csv);     // Crea la entidad desde CSV
}