// src/repository/FileManager.java
package repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidad para manejo de archivos de texto (CSV).
 */
public class FileManager {
    
    private static final String DATA_DIRECTORY = "data/";
    private static final String CHARSET = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());
    
    /**
     * Constructor que asegura que el directorio de datos existe.
     */
    public FileManager() {
        crearDirectorioSiNoExiste();
    }
    
    private void crearDirectorioSiNoExiste() {
        try {
            Path dataPath = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                LOGGER.info("Directorio de datos creado: " + DATA_DIRECTORY);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al crear directorio de datos", e);
        }
    }
    
    /**
     * Guarda una lista de líneas en un archivo (sobrescribe).
     */
    public void guardarLineas(String nombreArchivo, List<String> lineas) throws IOException {
        Path archivo = Paths.get(DATA_DIRECTORY + nombreArchivo);
        Files.write(archivo, lineas, StandardCharsets.UTF_8);
        LOGGER.info("Guardadas " + lineas.size() + " líneas en " + nombreArchivo);
    }
    
    /**
     * Agrega una línea al final del archivo.
     */
    public void agregarLinea(String nombreArchivo, String linea) throws IOException {
        Path archivo = Paths.get(DATA_DIRECTORY + nombreArchivo);
        
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8, 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(linea);
            writer.newLine();
        }
        LOGGER.info("Línea agregada a " + nombreArchivo);
    }
    
    /**
     * Carga todas las líneas de un archivo.
     * @return Lista de líneas (vacía si el archivo no existe)
     */
    public List<String> cargarLineas(String nombreArchivo) throws IOException {
        Path archivo = Paths.get(DATA_DIRECTORY + nombreArchivo);
        
        if (!Files.exists(archivo)) {
            LOGGER.info("Archivo no existe, se retorna lista vacía: " + nombreArchivo);
            return new ArrayList<>();
        }
        
        return Files.readAllLines(archivo, StandardCharsets.UTF_8);
    }
    
    /**
     * Verifica si un archivo existe.
     */
    public boolean existeArchivo(String nombreArchivo) {
        Path archivo = Paths.get(DATA_DIRECTORY + nombreArchivo);
        return Files.exists(archivo);
    }
    
    /**
     * Elimina un archivo.
     * @return true si el archivo fue eliminado o no existía
     */
    public boolean eliminarArchivo(String nombreArchivo) {
        try {
            Path archivo = Paths.get(DATA_DIRECTORY + nombreArchivo);
            boolean eliminado = Files.deleteIfExists(archivo);
            if (eliminado) {
                LOGGER.info("Archivo eliminado: " + nombreArchivo);
            }
            return eliminado;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al eliminar archivo: " + nombreArchivo, e);
            return false;
        }
    }
    
    /**
     * Limpia el contenido de un archivo (lo vacía).
     */
    public void limpiarArchivo(String nombreArchivo) throws IOException {
        Path archivo = Paths.get(DATA_DIRECTORY + nombreArchivo);
        if (Files.exists(archivo)) {
            Files.write(archivo, new ArrayList<>(), StandardCharsets.UTF_8);
            LOGGER.info("Archivo limpiado: " + nombreArchivo);
        }
    }
}