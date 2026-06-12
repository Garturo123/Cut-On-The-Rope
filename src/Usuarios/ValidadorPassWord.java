/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;


public class ValidadorPassWord {
    
    public static final int MIN_LENGTH = 8;
    
    public ValidationResult validar(String password) {
        if (password == null) return ValidationResult.invalid("La contraseña no puede ser nula");
        if (password.length() < MIN_LENGTH) return ValidationResult.invalid("Mínimo " + MIN_LENGTH + " caracteres");
        if (!tieneMayuscula(password)) return ValidationResult.invalid("Debe tener mayúscula");
        if (!tieneMinuscula(password)) return ValidationResult.invalid("Debe tener minúscula");
        if (!tieneNumero(password)) return ValidationResult.invalid("Debe tener número");
        if (!tieneEspecial(password)) return ValidationResult.invalid("Debe tener carácter especial");
        
        return ValidationResult.valid();
    }
    
    private boolean tieneMayuscula(String s) { return s.chars().anyMatch(Character::isUpperCase); }
    private boolean tieneMinuscula(String s) { return s.chars().anyMatch(Character::isLowerCase); }
    private boolean tieneNumero(String s) { return s.chars().anyMatch(Character::isDigit); }
    private boolean tieneEspecial(String s) { return s.chars().anyMatch(c -> !Character.isLetterOrDigit(c)); }
    
    public static class ValidationResult {
        public final boolean valido;
        public final String mensaje;
        
        private ValidationResult(boolean valido, String mensaje) {
            this.valido = valido;
            this.mensaje = mensaje;
        }
        
        public static ValidationResult valid() { return new ValidationResult(true, "OK"); }
        public static ValidationResult invalid(String msg) { return new ValidationResult(false, msg); }
    }
}