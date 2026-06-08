// service/ValidationException.java
package service;

/**
 * Excepción personalizada del dominio para errores de validación.
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private final String campoId;   // Identificador del campo en la UI
    private final String codigo;    // Código de error (ej. "REQUIRED", "INVALID_FORMAT")
    
    /**
     * Constructor con campo y mensaje.
     * @param campoId Identificador del componente en la UI
     * @param mensaje Mensaje descriptivo para el usuario
     */
    public ValidationException(String campoId, String mensaje) {
        super(mensaje);
        this.campoId = campoId;
        this.codigo = null;
    }
    
    /**
     * Constructor completo con campo, código y mensaje.
     * @param campoId Identificador del componente en la UI
     * @param codigo Código de error estandarizado
     * @param mensaje Mensaje descriptivo para el usuario
     */
    public ValidationException(String campoId, String codigo, String mensaje) {
        super(mensaje);
        this.campoId = campoId;
        this.codigo = codigo;
    }
    
    public String getCampoId() { return campoId; }
    public String getCodigo() { return codigo; }
    
    @Override
    public String toString() {
        return String.format("ValidationException{campo='%s', codigo='%s', mensaje='%s'}", 
                            campoId, codigo, getMessage());
    }
}