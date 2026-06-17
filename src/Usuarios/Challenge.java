package Usuarios;


public class Challenge {
    private final UsuarioRepo usuarioRepo;
    private final ActividadLogger logger;
    
    public Challenge (UsuarioRepo usuarioRepo, ActividadLogger logger) {
        this.usuarioRepo = usuarioRepo;
        this.logger = logger;
    }
    
    public String iniciar(Usuario actual, String usernameRival, String dificultad) {
        String rivalLimpio = limpiar(usernameRival);
        
        if (!usuarioRepo.existe(rivalLimpio)) return "El rival no existe";
        if (actual.getUsername().equals(rivalLimpio)) return "No puede retarse a sí mismo";
        if (!actual.getAmigosRivales().contains(rivalLimpio)) return "Solo puedes retar amigos";
        
        String log = String.format("Challenge amistoso contra %s | 10 niveles | 2 minutos | Gana mas niveles, desempate estrellas", rivalLimpio);
        
        actual.agregarReto(log);
        usuarioRepo.guardar(actual);
        
        Usuario rival = usuarioRepo.cargar(rivalLimpio);
        if (rival != null) {
            rival.agregarReto("Retado por " + actual.getUsername() + " | 10 niveles | 2 minutos");
            usuarioRepo.guardar(rival);
            logger.registrar(rivalLimpio, "game_activity.dat", "Fue retado por " + actual.getUsername());
        }
        
        logger.registrar(actual.getUsername(), "game_activity.dat", log);
        return "Challenge iniciado";
    }
    
    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }
}
