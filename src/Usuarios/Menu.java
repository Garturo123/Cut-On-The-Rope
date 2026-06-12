package Usuarios;

import java.util.ArrayList;

public class Menu {
    private final UsuarioRepo usuarioRepo;
    private final GestorNiveles puzzleRepo;
    private final ActividadLogger logger;
    private final Amigos amigosService;
    private final Challenge challengeService;
    private final Perfil perfilService;
    private final Avatar avatarService;
    private final ValidadorPassWord passwordValidator;
    
    private Usuario usuarioActual;
    private String idiomaSeleccionadoTemporal;
    
    public Menu() {
        this.usuarioRepo = new UsuarioRepo();
        this.puzzleRepo = new GestorNiveles();
        this.logger = new ActividadLogger();
        this.amigosService = new Amigos(usuarioRepo, logger);
        this.challengeService = new Challenge(usuarioRepo, logger);
        this.perfilService = new Perfil();
        this.avatarService = new Avatar();
        this.passwordValidator = new ValidadorPassWord();
        this.usuarioActual = null;
    }
    
    // ========== SESIÓN ==========
    public boolean haySesionActiva() { return usuarioActual != null; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    
    public String login(String username, String password) {
        String usernameLimpio = limpiar(username);
        if (!usuarioRepo.existe(usernameLimpio)) return "El usuario no existe";
        
        Usuario usuario = usuarioRepo.cargar(usernameLimpio);
        if (usuario == null) return "Error al cargar usuario";
        
        if (!usuario.getPasswordHash().equals(SecurityUtil.hash(password))) return "Contraseña incorrecta";
        if (!usuario.isCuentaActiva()) return "Cuenta desactivada. Reactívela para continuar";
        
        usuario.iniciarSesion();
        usuarioActual = usuario;
        aplicarIdiomaTemporal();
        usuarioRepo.guardar(usuarioActual);
        logger.registrar(usuarioActual.getUsername(), "account_activity.dat", "Login exitoso");
        
        return "Bienvenido";
    }
    
    public String crearUsuario(String username, String password, String nombreCompleto) {
        String usernameLimpio = limpiar(username);
        
        if (usernameLimpio.isEmpty() || password.isEmpty() || nombreCompleto.trim().isEmpty()) {
            return "Complete todos los campos";
        }
        if (usuarioRepo.existe(usernameLimpio)) return "Usuario ya existe";
        
        ValidadorPassWord.ValidationResult result = passwordValidator.validar(password);
        if (!result.valido) return result.mensaje;
        
        Usuario nuevo = new Usuario(usernameLimpio, SecurityUtil.hash(password), nombreCompleto.trim());
        if (idiomaSeleccionadoTemporal != null) nuevo.setIdioma(idiomaSeleccionadoTemporal);
        
        usuarioRepo.guardar(nuevo);
        logger.registrar(nuevo.getUsername(), "account_activity.dat", "Cuenta creada");
        return "Usuario creado correctamente";
    }
    
    public String logout() {
        if (usuarioActual == null) return "No hay sesión activa";
        logger.registrar(usuarioActual.getUsername(), "account_activity.dat", "Logout");
        usuarioActual.cerrarSesion();
        usuarioRepo.guardar(usuarioActual);
        usuarioActual = null;
        return "Sesión cerrada";
    }
    
    // ========== AMIGOS ==========
    public String agregarAmigo(String username) { return amigosService.agregarBidireccional(usuarioActual, username); }
    public String eliminarAmigos(ArrayList<String> amigos) { return amigosService.eliminar(usuarioActual, amigos); }
    public ArrayList<String> obtenerAmigos() { return usuarioActual == null ? new ArrayList<>() : usuarioActual.getAmigosRivales(); }
    public ArrayList<String> buscarUsuarios(String filtro) { return amigosService.buscarParaAgregar(usuarioActual, filtro); }
    
    // ========== CHALLENGES ==========
    public String iniciarChallenge(String rival, String dificultad) { return challengeService.iniciar(usuarioActual, rival, dificultad); }
    public int calcularPuntajeChallenge(String dificultad, boolean gano) { return challengeService.calcularPuntaje(dificultad, gano); }
    
    // ========== PERFIL ==========
    public String obtenerNombrePerfil() { return perfilService.obtenerNombreCompleto(usuarioActual); }
    public String obtenerUsernamePerfil() { return perfilService.obtenerUsername(usuarioActual); }
    public String obtenerStatusPerfil() { return perfilService.obtenerStatus(usuarioActual); }
    public String obtenerScorePerfil() { return perfilService.obtenerScore(usuarioActual); }
    public String actualizarPerfil(String avatar, int volumen, String idioma, String controles) {
        return perfilService.actualizar(usuarioActual, avatar, volumen, idioma, controles);
    }
    
    // ========== AVATARES ==========
    public String guardarAvatar(String avatar, String color) { return avatarService.guardar(usuarioActual, avatar, color); }
    public String obtenerAvatarPath() { return perfilService.obtenerAvatarPath(usuarioActual); }
    public ArrayList<String> obtenerAvatares() { return avatarService.obtenerAvataresDisponibles(); }
    public ArrayList<String> obtenerColoresAvatar() { return avatarService.obtenerColoresDisponibles(); }
    
    // ========== PUZZLES ==========
    public boolean puedeJugarNivel(int nivel) { return usuarioActual != null && nivel <= usuarioActual.getNivelDesbloqueado(); }
    public boolean nivelCompletado(int nivel) { return obtenerPuzzles().stream().anyMatch(p -> p.getNivel() == nivel && p.isCompletado()); }
    
    public ArrayList<Niveles> obtenerPuzzles() {
        if (usuarioActual == null) return new ArrayList<>();
        ArrayList<Niveles> puzzles = puzzleRepo.cargar(usuarioActual.getUsername());
        puzzleRepo.guardar(usuarioActual.getUsername(), puzzles);
        return puzzles;
    }
    
    public String completarNivel(int nivel, int puntaje, long tiempo) {
        ArrayList<Niveles> puzzles = obtenerPuzzles();
        for (Niveles p : puzzles) {
            if (p.getNivel() == nivel) {
                p.completarNivel(puntaje, tiempo);
                puzzleRepo.guardar(usuarioActual.getUsername(), puzzles);
                logger.registrar(usuarioActual.getUsername(), "game_activity.dat", "Nivel " + nivel + " completado");
                return "Nivel completado";
            }
        }
        return "Nivel no encontrado";
    }
    
    // ========== ACTIVIDADES ==========
    public ArrayList<Actividad> obtenerAccountActivity() { return logger.obtener(usuarioActual.getUsername(), "account_activity.dat"); }
    public ArrayList<Actividad> obtenerGameActivity() { return logger.obtener(usuarioActual.getUsername(), "game_activity.dat"); }
    
    // ========== AUDIO ==========
    public void actualizarConfigAudio(int volSFX, int volMus, boolean sfxAct, boolean musAct, double pos) {
        if (usuarioActual != null) {
            usuarioActual.actualizarConfigAudio(volSFX, volMus, sfxAct, musAct, pos);
            usuarioRepo.guardar(usuarioActual);
        }
    }
    
    // ========== IDIOMA ==========
    public void seleccionarIdiomaTemporal(String idioma) {
        idiomaSeleccionadoTemporal = (idioma != null && (idioma.equalsIgnoreCase("English") || idioma.equalsIgnoreCase("Spanish"))) 
            ? idioma : null;
    }
    
    private void aplicarIdiomaTemporal() {
        if (usuarioActual != null && idiomaSeleccionadoTemporal != null) {
            usuarioActual.setIdioma(idiomaSeleccionadoTemporal);
            usuarioRepo.guardar(usuarioActual);
        }
    }
    
    // ========== UTILS ==========
    private String limpiar(String texto) { return texto == null ? "" : texto.trim().toLowerCase(); }
}
