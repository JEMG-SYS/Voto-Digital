// service/VotacionController.java
package service;

import model.Candidato;
import model.Votante;
import repository.VotoRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controlador que actúa como intermediario entre la UI y los servicios.
 */
public class VotacionController {
    
    private final VotacionService votacionService;
    private final AutenticacionService authService;
    
    /**
     * Constructor por defecto que crea todas las dependencias.
     * Útil para uso en UI simple.
     */
    public VotacionController() {
        VotoRepository votoRepository = new VotoRepository();
        this.votacionService = new VotacionService(votoRepository);
        this.authService = new AutenticacionService();
    }
    
    /**
     * Constructor con inyección de dependencias (para pruebas y mayor flexibilidad).
     * Aplica DIP: Depende de abstracciones de servicios.
     * 
     * @param votacionService Servicio de lógica de votación
     * @param authService Servicio de autenticación
     */
    public VotacionController(VotacionService votacionService, AutenticacionService authService) {
        this.votacionService = Objects.requireNonNull(votacionService, "VotacionService no puede ser nulo");
        this.authService = Objects.requireNonNull(authService, "AutenticacionService no puede ser nulo");
    }
    
    // ==================== AUTENTICACIÓN ====================
    
    /**
     * Autentica al administrador.
     * @return true si las credenciales son correctas
     */
    public boolean loginAdmin(String usuario, String password) throws ValidationException {
        return authService.autenticar(usuario, password);
    }
    
    // ==================== VOTACIÓN ====================
    
    /**
     * Procesa el voto de un votante.
     * @return true si se registró correctamente
     */
    public boolean procesarVoto(String cedula, String idCandidato) throws ValidationException {
        return votacionService.registrarVoto(cedula, idCandidato);
    }
    
    /**
     * Obtiene los resultados de la votación.
     */
    public Map<String, Integer> obtenerResultados() {
        return votacionService.obtenerResultados();
    }
    
    /**
     * Obtiene resultados ordenados por votos (de mayor a menor).
     */
    public Map<String, Integer> obtenerResultadosOrdenados() {
        return votacionService.obtenerResultadosOrdenados();
    }
    
    // ==================== DATOS PARA UI ====================
    
    /**
     * Obtiene la lista de candidatos para mostrar en la UI.
     */
    public List<Candidato> getCandidatos() {
        return votacionService.getCandidatos();
    }
    
    /**
     * Busca un votante por su cédula.
     */
    public Votante buscarVotante(String cedula) {
        return votacionService.buscarVotantePorCedula(cedula);
    }
    
    // ==================== ESTADÍSTICAS DASHBOARD ====================
    
    public int getTotalVotantes() {
        return votacionService.getTotalVotantes();
    }
    
    public int getVotosEmitidos() {
        return votacionService.getVotosEmitidos();
    }
    
    public int getVotosPendientes() {
        return votacionService.getVotosPendientes();
    }
    
    public double getPorcentajeParticipacion() {
        return votacionService.getPorcentajeParticipacion();
    }
    
    // ==================== ADMINISTRACIÓN ====================
    
    /**
     * Reinicia la votación (útil para pruebas o nuevas elecciones).
     */
    public void reiniciarVotacion() {
        votacionService.reiniciarVotacion();
    }
}