// src/repository/VotoRepository.java
package repository;

import model.Voto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio para la persistencia de votos en archivo CSV.
 * Implementa el patrón Repository.
 */
public class VotoRepository implements Persistible<Voto> {
    
    private static final String ARCHIVO_VOTOS = "votos.csv";
    private static final String CSV_SEPARATOR = ";";
    private static final Logger LOGGER = Logger.getLogger(VotoRepository.class.getName());
    
    private final FileManager fileManager;
    
    /**
     * Constructor con FileManager por defecto.
     */
    public VotoRepository() {
        this.fileManager = new FileManager();
    }
    
    /**
     * Constructor con inyección de dependencias (para pruebas).
     * Aplica DIP.
     */
    public VotoRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    // ==================== CRUD PRINCIPAL ====================
    
    /**
     * Guarda un nuevo voto en el archivo.
     */
    public void guardarVoto(Voto voto) {
        try {
            String linea = toCSV(voto);
            fileManager.agregarLinea(ARCHIVO_VOTOS, linea);
            LOGGER.info("Voto guardado: " + voto.getId());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar voto: " + voto.getId(), e);
            throw new RuntimeException("No se pudo guardar el voto", e);
        }
    }
    
    /**
     * Carga todos los votos almacenados.
     * @return Lista de todos los votos (vacía si no hay archivo)
     */
    public List<Voto> cargarTodosLosVotos() {
        List<Voto> votos = new ArrayList<>();
        
        try {
            List<String> lineas = fileManager.cargarLineas(ARCHIVO_VOTOS);
            for (String linea : lineas) {
                if (linea != null && !linea.trim().isEmpty()) {
                    Voto voto = fromCSV(linea);
                    if (voto != null) {
                        votos.add(voto);
                    }
                }
            }
            LOGGER.info("Cargados " + votos.size() + " votos desde archivo");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al cargar votos", e);
        }
        
        return votos;
    }
    
    /**
     * Busca un voto por su ID.
     * @return Optional con el voto encontrado o vacío
     */
    public Optional<Voto> buscarVotoPorId(String id) {
        return cargarTodosLosVotos().stream()
            .filter(voto -> voto.getId().equals(id))
            .findFirst();
    }
    
    /**
     * Busca votos por cédula de votante.
     */
    public List<Voto> buscarVotosPorVotante(String cedulaVotante) {
        return cargarTodosLosVotos().stream()
            .filter(voto -> voto.getVotanteId().equals(cedulaVotante))
            .toList();
    }
    
    /**
     * Busca votos por ID de candidato.
     */
    public List<Voto> buscarVotosPorCandidato(String idCandidato) {
        return cargarTodosLosVotos().stream()
            .filter(voto -> voto.getOpcionId().equals(idCandidato))
            .toList();
    }
    
    /**
     * Elimina todos los votos (limpia el archivo).
     */
    public void limpiarVotos() {
        try {
            fileManager.limpiarArchivo(ARCHIVO_VOTOS);
            LOGGER.info("Todos los votos han sido eliminados");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al limpiar votos", e);
            throw new RuntimeException("No se pudieron limpiar los votos", e);
        }
    }
    
    /**
     * Obtiene el total de votos almacenados.
     */
    public int contarVotos() {
        return cargarTodosLosVotos().size();
    }
    
    /**
     * Verifica si un votante ya ha votado.
     */
    public boolean votanteYaVoto(String cedulaVotante) {
        return !buscarVotosPorVotante(cedulaVotante).isEmpty();
    }
    
    // ==================== IMPLEMENTACIÓN DE Persistible ====================
    
    @Override
    public String toCSV(Voto voto) {
        // Formato: id;votanteId;opcionId;timestamp;hash
        return String.join(CSV_SEPARATOR,
            voto.getId(),
            voto.getVotanteId(),
            voto.getOpcionId(),
            voto.getTimestamp().toString(),
            voto.getHash()
        );
    }
    
    @Override
    public Voto fromCSV(String linea) {
        try {
            String[] partes = linea.split(CSV_SEPARATOR);
            
            if (partes.length < 5) {
                LOGGER.warning("Línea CSV inválida: " + linea);
                return null;
            }
            
            String id = partes[0];
            String votanteId = partes[1];
            String opcionId = partes[2];
            LocalDateTime timestamp = LocalDateTime.parse(partes[3]);
            String hash = partes[4];
            
            return new Voto(id, votanteId, opcionId, timestamp, hash);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al parsear CSV: " + linea, e);
            return null;
        }
    }
}