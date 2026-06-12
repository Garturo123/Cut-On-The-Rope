package Usuarios;


import java.util.HashMap;
import java.util.Map;

public class Challenge {
    private final UsuarioRepo usuarioRepo;
    private final ActividadLogger logger;
    private final Map<String, Integer> puntajesPorDificultad;
    
    public Challenge (UsuarioRepo usuarioRepo, ActividadLogger logger) {
        this.usuarioRepo = usuarioRepo;
        this.logger = logger;
        this.puntajesPorDificultad = new HashMap<>();
        inicializarPuntajes();
    }
    
    private void inicializarPuntajes() {
        puntajesPorDificultad.put("NEON CIRCUIT", 50);
        puntajesPorDificultad.put("POWER GRID", 100);
        puntajesPorDificultad.put("VOLTAGE RUN", 150);
        puntajesPorDificultad.put("ELECTRIC DRIFT", 200);
        puntajesPorDificultad.put("OVERLOAD", 250);
    }
    
    public String iniciar(Usuario actual, String usernameRival, String dificultad) {
        String rivalLimpio = limpiar(usernameRival);
        
        if (!usuarioRepo.existe(rivalLimpio)) return "El rival no existe";
        if (actual.getUsername().equals(rivalLimpio)) return "No puede retarse a sí mismo";
        
        int nivel = obtenerNivelAleatorio(dificultad);
        String log = String.format("Challenge contra %s | Dificultad: %s | Nivel: %d", rivalLimpio, dificultad, nivel);
        
        actual.agregarReto(log);
        usuarioRepo.guardar(actual);
        
        Usuario rival = usuarioRepo.cargar(rivalLimpio);
        if (rival != null) {
            rival.agregarReto("Retado por " + actual.getUsername() + " | " + log);
            usuarioRepo.guardar(rival);
            logger.registrar(rivalLimpio, "game_activity.dat", "Fue retado por " + actual.getUsername());
        }
        
        logger.registrar(actual.getUsername(), "game_activity.dat", log);
        return "Challenge iniciado";
    }
    
    public int calcularPuntaje(String dificultad, boolean gano) {
        int base = puntajesPorDificultad.getOrDefault(dificultad.toUpperCase(), 50);
        return gano ? base + 50 : base;
    }
    
    private int obtenerNivelAleatorio(String dificultad) {
        int base = obtenerNumeroDificultad(dificultad);
        return (base * 2 - 1) + (int)(Math.random() * 2);
    }
    
    private int obtenerNumeroDificultad(String dificultad) {
        switch (dificultad.toUpperCase()) {
            case "NEON CIRCUIT": return 1;
            case "POWER GRID": return 2;
            case "VOLTAGE RUN": return 3;
            case "ELECTRIC DRIFT": return 4;
            case "OVERLOAD": return 5;
            default: return 1;
        }
    }
    
    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
}