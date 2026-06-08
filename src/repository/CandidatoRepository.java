// src/repository/CandidatoRepository.java
package repository;

import model.Candidato;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio para la persistencia de candidatos en archivo CSV.
 */
public class CandidatoRepository implements Persistible<Candidato> {
    
    private static final String ARCHIVO_CANDIDATOS = "candidatos.csv";
    private static final String CSV_SEPARATOR = ";";
    private static final Logger LOGGER = Logger.getLogger(CandidatoRepository.class.getName());
    
    private final FileManager fileManager;
    
    public CandidatoRepository() {
        this.fileManager = new FileManager();
    }
    
    public CandidatoRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    /**
     * Guarda un candidato en el archivo.
     */
    public void guardarCandidato(Candidato candidato) {
        try {
            String linea = toCSV(candidato);
            fileManager.agregarLinea(ARCHIVO_CANDIDATOS, linea);
            LOGGER.info("Candidato guardado: " + candidato.getId());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar candidato", e);
            throw new RuntimeException("No se pudo guardar el candidato", e);
        }
    }
    
    /**
     * Carga todos los candidatos almacenados.
     */
    public List<Candidato> cargarTodosLosCandidatos() {
        List<Candidato> candidatos = new ArrayList<>();
        
        try {
            List<String> lineas = fileManager.cargarLineas(ARCHIVO_CANDIDATOS);
            for (String linea : lineas) {
                if (linea != null && !linea.trim().isEmpty()) {
                    Candidato candidato = fromCSV(linea);
                    if (candidato != null) {
                        candidatos.add(candidato);
                    }
                }
            }
            LOGGER.info("Cargados " + candidatos.size() + " candidatos");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al cargar candidatos", e);
        }
        
        return candidatos;
    }
    
    /**
     * Busca un candidato por ID.
     */
    public Optional<Candidato> buscarCandidatoPorId(String id) {
        return cargarTodosLosCandidatos().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst();
    }
    
    /**
     * Guarda o actualiza un candidato (UPSERT).
     */
    public void guardarOActualizar(Candidato candidato) {
        List<Candidato> candidatos = cargarTodosLosCandidatos();
        boolean existe = false;
        
        for (int i = 0; i < candidatos.size(); i++) {
            if (candidatos.get(i).getId().equals(candidato.getId())) {
                candidatos.set(i, candidato);
                existe = true;
                break;
            }
        }
        
        if (!existe) {
            candidatos.add(candidato);
        }
        
        guardarTodos(candidatos);
    }
    
    /**
     * Guarda una lista completa de candidatos (sobrescribe el archivo).
     */
    public void guardarTodos(List<Candidato> candidatos) {
        try {
            List<String> lineas = new ArrayList<>();
            for (Candidato c : candidatos) {
                lineas.add(toCSV(c));
            }
            fileManager.guardarLineas(ARCHIVO_CANDIDATOS, lineas);
            LOGGER.info("Guardados " + candidatos.size() + " candidatos");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar candidatos", e);
            throw new RuntimeException("No se pudieron guardar los candidatos", e);
        }
    }
    
    /**
     * Elimina un candidato por ID.
     */
    public boolean eliminarCandidato(String id) {
        List<Candidato> candidatos = cargarTodosLosCandidatos();
        boolean eliminado = candidatos.removeIf(c -> c.getId().equals(id));
        
        if (eliminado) {
            guardarTodos(candidatos);
            LOGGER.info("Candidato eliminado: " + id);
        }
        
        return eliminado;
    }
    
    @Override
    public String toCSV(Candidato candidato) {
        // Formato: id;nombreCompleto;identificacion;email;partido;biografia;orden;votosRecibidos
        return String.join(CSV_SEPARATOR,
            candidato.getId(),
            candidato.getNombreCompleto(),
            candidato.getIdentificacion(),
            candidato.getEmail(),
            candidato.getPartido(),
            candidato.getBiografia(),
            String.valueOf(candidato.getOrden()),
            String.valueOf(candidato.getVotosRecibidos())
        );
    }
    
    @Override
    public Candidato fromCSV(String linea) {
        try {
            String[] partes = linea.split(CSV_SEPARATOR);
            
            if (partes.length < 8) {
                LOGGER.warning("Línea CSV inválida para candidato: " + linea);
                return null;
            }
            
            String id = partes[0];
            String nombre = partes[1];
            String identificacion = partes[2];
            String email = partes[3];
            String partido = partes[4];
            String biografia = partes[5];
            int orden = Integer.parseInt(partes[6]);
            int votosRecibidos = Integer.parseInt(partes[7]);
            
            Candidato candidato = new Candidato(id, nombre, identificacion, email, partido, biografia, orden);
            candidato.setVotosRecibidos(votosRecibidos);
            
            return candidato;
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al parsear candidato: " + linea, e);
            return null;
        }
    }
}