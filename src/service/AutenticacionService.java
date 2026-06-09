// service/AutenticacionService.java
package service;

/**
 * Servicio de autenticación para el administrador.
 * Credenciales hardcodeadas según especificación FASE 2.
 */
public class AutenticacionService {
    
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";
    
    private static final String ERROR_REQUIRED = "REQUIRED";
    private static final String ERROR_INVALID_USER = "INVALID_USER";
    private static final String ERROR_INVALID_PASSWORD = "INVALID_PASSWORD";
    
    /**
     * 
     * @param usuario Nombre de usuario ingresado
     * @param password Contraseña ingresada
     * @return true si las credenciales son correctas
     * @throws ValidationException Si algún campo está vacío o las credenciales son inválidas
     */
    public boolean autenticar(String usuario, String password) throws ValidationException {
        
        // Validar usuario no vacío
        validarCampoNoVacio(usuario, "txtUsuario", ERROR_REQUIRED, "El usuario es obligatorio");
        
        // Validar contraseña no vacía
        validarCampoNoVacio(password, "txtPassword", ERROR_REQUIRED, "La contraseña es obligatoria");
        
        // Validar usuario correcto
        if (!ADMIN_USER.equals(usuario)) {
            throw new ValidationException("txtUsuario", ERROR_INVALID_USER, "Usuario incorrecto");
        }
        
        // Validar contraseña correcta
        if (!ADMIN_PASS.equals(password)) {
            throw new ValidationException("txtPassword", ERROR_INVALID_PASSWORD, "Contraseña incorrecta");
        }
        
        return true;
    }
    
    private void validarCampoNoVacio(String valor, String campoId, String codigo, String mensaje) 
            throws ValidationException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidationException(campoId, codigo, mensaje);
        }
    }
}