// src/repository/VotanteRepository.java
package repository;

import model.Votante;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio para la persistencia de votantes en archivo CSV.
 */
public class VotanteRepository implements Persistible<Votante> {
    
    private static final String ARCHIVO_VOTANTES = "votantes.csv";
    private static final String CSV_SEPARATOR = ";";
    private static final Logger LOGGER = Logger.getLogger(VotanteRepository.class.getName());
    
    private final FileManager fileManager;
    
    public VotanteRepository() {
        this.fileManager = new FileManager();
    }
    
    public VotanteRepository(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    /**
     * Guarda un votante en el archivo.
     */
    public void guardarVotante(Votante votante) {
        try {
            String linea = toCSV(votante);
            fileManager.agregarLinea(ARCHIVO_VOTANTES, linea);
            LOGGER.info("Votante guardado: " + votante.getId());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar votante", e);
            throw new RuntimeException("No se pudo guardar el votante", e);
        }
    }
    
    /**
     * Carga todos los votantes almacenados.
     */
    public List<Votante> cargarTodosLosVotantes() {
        List<Votante> votantes = new ArrayList<>();
        
        try {
            List<String> lineas = fileManager.cargarLineas(ARCHIVO_VOTANTES);
            for (String linea : lineas) {
                if (linea != null && !linea.trim().isEmpty()) {
                    Votante votante = fromCSV(linea);
                    if (votante != null) {
                        votantes.add(votante);
                    }
                }
            }
            LOGGER.info("Cargados " + votantes.size() + " votantes");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al cargar votantes", e);
        }
        
        return votantes;
    }
    
    /**
     * Busca un votante por ID.
     */
    public Optional<Votante> buscarVotantePorId(String id) {
        return cargarTodosLosVotantes().stream()
            .filter(v -> v.getId().equals(id))
            .findFirst();
    }
    
    /**
     * Busca un votante por identificación (cédula).
     */
    public Optional<Votante> buscarVotantePorIdentificacion(String identificacion) {
        return cargarTodosLosVotantes().stream()
            .filter(v -> v.getIdentificacion().equals(identificacion))
            .findFirst();
    }
    
    /**
     * Verifica si ya existe un votante con esa identificación.
     */
    public boolean existePorIdentificacion(String identificacion) {
        return buscarVotantePorIdentificacion(identificacion).isPresent();
    }
    
    /**
     * Verifica si ya existe un votante con ese email.
     */
    public boolean existePorEmail(String email) {
        return cargarTodosLosVotantes().stream()
            .anyMatch(v -> v.getEmail().equals(email));
    }
    
    /**
     * Guarda o actualiza un votante (UPSERT).
     */
    public void guardarOActualizar(Votante votante) {
        List<Votante> votantes = cargarTodosLosVotantes();
        boolean existe = false;
        
        for (int i = 0; i < votantes.size(); i++) {
            if (votantes.get(i).getId().equals(votante.getId())) {
                votantes.set(i, votante);
                existe = true;
                break;
            }
        }
        
        if (!existe) {
            votantes.add(votante);
        }
        
        guardarTodos(votantes);
    }
    
    /**
     * Guarda una lista completa de votantes (sobrescribe el archivo).
     */
    public void guardarTodos(List<Votante> votantes) {
        try {
            List<String> lineas = new ArrayList<>();
            for (Votante v : votantes) {
                lineas.add(toCSV(v));
            }
            fileManager.guardarLineas(ARCHIVO_VOTANTES, lineas);
            LOGGER.info("Guardados " + votantes.size() + " votantes");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar votantes", e);
            throw new RuntimeException("No se pudieron guardar los votantes", e);
        }
    }
    
    @Override
    public String toCSV(Votante votante) {
        // Formato: id;nombreCompleto;identificacion;email;fechaNacimiento;direccion;telefono;genero;haVotado;fechaRegistro
        return String.join(CSV_SEPARATOR,
            votante.getId(),
            votante.getNombreCompleto(),
            votante.getIdentificacion(),
            votante.getEmail(),
            votante.getFechaNacimiento() != null ? votante.getFechaNacimiento().toString() : "",
            votante.getDireccion(),
            votante.getTelefono(),
            votante.getGenero(),
            String.valueOf(votante.isHaVotado()),
            votante.getFechaRegistro().toString()
        );
    }
    
    @Override
    public Votante fromCSV(String linea) {
        try {
            String[] partes = linea.split(CSV_SEPARATOR);
            
            if (partes.length < 10) {
                LOGGER.warning("Línea CSV inválida para votante: " + linea);
                return null;
            }
            
            String id = partes[0];
            String nombre = partes[1];
            String identificacion = partes[2];
            String email = partes[3];
            LocalDate fechaNacimiento = partes[4].isEmpty() ? null : LocalDate.parse(partes[4]);
            String direccion = partes[5];
            String telefono = partes[6];
            String genero = partes[7];
            boolean haVotado = Boolean.parseBoolean(partes[8]);
            LocalDateTime fechaRegistro = LocalDateTime.parse(partes[9]);
            
            Votante votante = new Votante(id, nombre, identificacion, email, fechaNacimiento, direccion, telefono, genero);
            votante.setHaVotado(haVotado);
            
            return votante;
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al parsear votante: " + linea, e);
            return null;
        }
    }
}