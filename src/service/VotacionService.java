package service;

import model.Candidato;
import model.Votante;
import model.Voto;
import repository.VotoRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal de lógica de negocio para la votación.
 */
public class VotacionService {
    
    private final List<Candidato> candidatos;
    private final List<Votante> votantesRegistrados;
    private final VotoRepository votoRepository;
    
    // Constantes para mensajes de error
    private static final String CAMPO_CEDULA = "txtCedula";
    private static final String CAMPO_CANDIDATO = "cmbCandidatos";
    
    /**
     * Constructor con inyección de dependencia (DIP).
     * @param votoRepository Repositorio para persistencia de votos
     */
    public VotacionService(VotoRepository votoRepository) {
        this.votoRepository = Objects.requireNonNull(votoRepository, "VotoRepository no puede ser nulo");
        this.candidatos = new ArrayList<>();
        this.votantesRegistrados = new ArrayList<>();
        inicializarDatos();
    }
    
    // ==================== MÉTODOS PÚBLICOS PRINCIPALES ====================
    
    /**
     * Registra un voto en el sistema con todas las validaciones necesarias.
     * @param cedula Identificación del votante
     * @param idCandidato ID del candidato seleccionado
     * @return true si el voto se registró correctamente
     * @throws ValidationException Si alguna validación falla
     */
    public boolean registrarVoto(String cedula, String idCandidato) throws ValidationException {
        
        // 1. Validar cédula
        String cedulaValidada = validarYCederCedula(cedula);
        
        // 2. Buscar y validar votante
        Votante votante = buscarVotanteOValidar(cedulaValidada);
        
        // 3. Validar que no haya votado (Experto en información - el votante sabe)
        if (votante.isHaVotado()) {
            throw new ValidationException(CAMPO_CEDULA, "ALREADY_VOTED", 
                "Este votante ya emitió su voto");
        }
        
        // 4. Validar candidato
        Candidato candidato = validarYBuscarCandidato(idCandidato);
        
        // 5. Persistir voto
        Voto nuevoVoto = crearNuevoVoto(votante, candidato);
        votoRepository.guardarVoto(nuevoVoto);
        
        // 6. Actualizar estado (Experto en información - cada objeto se actualiza a sí mismo)
        candidato.incrementarVoto();
        votante.registrarVoto();
        
        return true;
    }
    
    /**
     * Obtiene los resultados de la votación.
     * @return Mapa no modificable con nombre del candidato y votos
     */
    public Map<String, Integer> obtenerResultados() {
        Map<String, Integer> resultados = new LinkedHashMap<>();
        
        for (Candidato candidato : candidatos) {
            String clave = construirClaveResultado(candidato);
            resultados.put(clave, candidato.getVotosRecibidos());
        }
        
        return Collections.unmodifiableMap(resultados);
    }
    
    /**
     * Obtiene resultados ordenados por votos (de mayor a menor).
     * Aplica OCP: Nuevo método sin modificar obtenerResultados()
     */
    public Map<String, Integer> obtenerResultadosOrdenados() {
        return obtenerResultados().entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> v1,
                LinkedHashMap::new
            ));
    }
    
    // ==================== MÉTODOS GETTERS ====================
    
    public List<Candidato> getCandidatos() { 
        return Collections.unmodifiableList(candidatos); 
    }
    
    public List<Votante> getVotantesRegistrados() {
        return Collections.unmodifiableList(votantesRegistrados);
    }
    
    public int getTotalVotantes() { 
        return votantesRegistrados.size(); 
    }
    
    public int getVotosEmitidos() { 
        return (int) votantesRegistrados.stream()
            .filter(Votante::isHaVotado)
            .count(); 
    }
    
    public int getVotosPendientes() {
        return getTotalVotantes() - getVotosEmitidos();
    }
    
    public double getPorcentajeParticipacion() {
        if (votantesRegistrados.isEmpty()) return 0.0;
        return (getVotosEmitidos() * 100.0) / votantesRegistrados.size();
    }
    
    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    public Votante buscarVotantePorCedula(String cedula) {
        if (cedula == null) return null;
        return votantesRegistrados.stream()
            .filter(votante -> votante.getIdentificacion().equals(cedula))
            .findFirst()
            .orElse(null);
    }
    
    public Candidato buscarCandidatoPorId(String id) {
        if (id == null) return null;
        return candidatos.stream()
            .filter(candidato -> candidato.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // ==================== MÉTODOS DE ADMINISTRACIÓN ====================
    
    /**
     * Reinicia la votación (limpia todos los votos y contadores).
     * Aplica Alta cohesión: Método relacionado con la gestión de votación.
     */
    public void reiniciarVotacion() {
        candidatos.forEach(candidato -> candidato.setVotosRecibidos(0));
        votantesRegistrados.forEach(votante -> votante.setHaVotado(false));
        votoRepository.limpiarVotos();
    }
    
    // ==================== MÉTODOS PRIVADOS (ENCAPSULAMIENTO) ====================
    
    private void inicializarDatos() {
        cargarCandidatos();
        cargarVotantes();
        cargarVotosExistentes();
    }
    
    private void cargarCandidatos() {
        candidatos.add(new Candidato("C001", "Juan Pérez", "Rector", 
            "Experiencia, liderazgo y compromiso con la educación", 1));
        candidatos.add(new Candidato("C002", "María Gómez", "Rectora", 
            "Innovación, cambio y participación ciudadana", 2));
        candidatos.add(new Candidato("C003", "Carlos López", "Vicerrector", 
            "Compromiso académico y excelencia educativa", 3));
        candidatos.add(new Candidato("C004", "Ana Rodríguez", "Vicerrectora", 
            "Bienestar estudiantil y desarrollo integral", 4));
    }
    
    private void cargarVotantes() {
        votantesRegistrados.add(new Votante("V001", "Ana Martínez", "1001", "ana@mail.com"));
        votantesRegistrados.add(new Votante("V002", "Luis Rodríguez", "1002", "luis@mail.com"));
        votantesRegistrados.add(new Votante("V003", "Carla Sánchez", "1003", "carla@mail.com"));
        votantesRegistrados.add(new Votante("V004", "Pedro Ramírez", "1004", "pedro@mail.com"));
        votantesRegistrados.add(new Votante("V005", "Laura Torres", "1005", "laura@mail.com"));
    }
    
    private void cargarVotosExistentes() {
        List<Voto> votosExistentes = votoRepository.cargarTodosLosVotos();
        for (Voto voto : votosExistentes) {
            Candidato candidato = buscarCandidatoPorId(voto.getOpcionId());
            if (candidato != null) {
                candidato.incrementarVoto();
            }
            Votante votante = buscarVotantePorCedula(voto.getVotanteId());
            if (votante != null) {
                votante.setHaVotado(true);
            }
        }
    }
    
    private String validarYCederCedula(String cedula) throws ValidationException {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new ValidationException(CAMPO_CEDULA, "REQUIRED", "La cédula es obligatoria");
        }
        
        String cedulaLimpia = cedula.trim();
        
        if (!cedulaLimpia.matches("^[0-9]+$")) {
            throw new ValidationException(CAMPO_CEDULA, "INVALID_FORMAT", 
                "La cédula solo debe contener números");
        }
        
        return cedulaLimpia;
    }
    
    private Votante buscarVotanteOValidar(String cedula) throws ValidationException {
        Votante votante = buscarVotantePorCedula(cedula);
        if (votante == null) {
            throw new ValidationException(CAMPO_CEDULA, "VOTANTE_NOT_FOUND", 
                "No se encontró un votante con la cédula: " + cedula);
        }
        return votante;
    }
    
    private Candidato validarYBuscarCandidato(String idCandidato) throws ValidationException {
        if (idCandidato == null || idCandidato.trim().isEmpty()) {
            throw new ValidationException(CAMPO_CANDIDATO, "REQUIRED", 
                "Debe seleccionar una opción de voto");
        }
        
        Candidato candidato = buscarCandidatoPorId(idCandidato);
        if (candidato == null) {
            throw new ValidationException(CAMPO_CANDIDATO, "CANDIDATO_NOT_FOUND", 
                "El candidato seleccionado no existe");
        }
        return candidato;
    }
    
    private Voto crearNuevoVoto(Votante votante, Candidato candidato) {
        return new Voto(
            UUID.randomUUID().toString(),
            votante.getIdentificacion(),
            candidato.getId(),
            LocalDateTime.now()
        );
    }
    
    private String construirClaveResultado(Candidato candidato) {
        return candidato.getNombre() + " - " + candidato.getPartido();
    }
}