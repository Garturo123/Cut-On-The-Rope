package ctr.entity;

import Audio.Manager;
import Usuarios.AuthService;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import Usuarios.UsuarioRepo;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;

public class PerfilEntity extends Entity {
    
    private Button btnVolver;
    private Button btnCerrarSesion;
    private Button btnEliminarCuenta;
    private Button btnCambiarAvatar;
    private Button btnConfigAudio;
    private Button btnAmigos;
    private Button btnStats;
    
    private AuthService authService;
    private SessionManager sessionManager;
    private UsuarioRepo usuarioRepo;
    private Manager audioManager;
    
    private BufferedImage avatarActual;
    private String[] infoLines;
    private boolean necesitaActualizar = true;
    
    public PerfilEntity(Scene scene, UsuarioRepo usuarioRepo, SessionManager sessionManager, Manager audioManager) {
        super(scene);
        this.usuarioRepo = usuarioRepo;
        this.sessionManager = sessionManager;
        this.authService = new AuthService(usuarioRepo, sessionManager);
        this.audioManager = audioManager;
        
        int xCol1 = 105;
        int xCol2 = 355;
        int yStart = 480;
        
        btnVolver = new Button(scene, "Back", 50, 28, 50, 535);
        btnCerrarSesion = new Button(scene, "Logout", 50, 28, xCol1, yStart);
        btnEliminarCuenta = new Button(scene, "Delete", 55, 28, xCol1 + 185, yStart);
        btnCambiarAvatar = new Button(scene, "Avatar", 55, 28, xCol2 + 185, yStart);
        btnConfigAudio = new Button(scene, "Audio", 55, 28, xCol1, yStart + 50);
        btnAmigos = new Button(scene, "Friends", 50, 28, xCol1 + 185, yStart + 50);
        btnStats = new Button(scene, "Stats", 55, 28, xCol2 + 185, yStart + 50);
        
        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
        btnCerrarSesion.setListener(() -> {
            authService.logout();
            scene.cambiarAState(GameState.MENU_PRINCIPAL);
        });
        btnEliminarCuenta.setListener(() -> {
            eliminarCuentaActual();
            scene.cambiarAState(GameState.MENU_PRINCIPAL);
        });
        btnCambiarAvatar.setListener(() -> scene.cambiarAState(GameState.AVATAR_SELECTOR));
        btnConfigAudio.setListener(() -> scene.cambiarAState(GameState.AUDIO_CONFIG));
        btnAmigos.setListener(() -> scene.cambiarAState(GameState.AMIGOS_LIST));
        btnStats.setListener(() -> scene.cambiarAState(GameState.STATS));
    }
    
    private void eliminarCuentaActual() {
        Usuario usuario = sessionManager.getUsuarioActual();
        if (usuario != null) {
            usuarioRepo.eliminarCarpeta(usuario.getUsername());
            sessionManager.cerrarSesion();
        }
    }
    
    private void actualizarInfo() {
        Usuario usuario = sessionManager.getUsuarioActual();
        if (usuario == null) return;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        infoLines = new String[] {
            "═══════════════════════════════════════",
            "USER PROFILE",
            "═══════════════════════════════════════",
            "Username: " + usuario.getUsername(),
            "Name: " + usuario.getNombreCompleto(),
            "Status: " + (usuario.isCuentaActiva() ? "Active" : "Inactive"),
            "═══════════════════════════════════════",
            "STATISTICS",
            "═══════════════════════════════════════",
            "Total Score: " + usuario.getPuntuacionGeneral(),
            "Levels Completed: " + usuario.getNivelesCompletados(),
            "Challenges Won: " + usuario.getRetosGanados(),
            "Friends: " + usuario.getAmigosRivales().size(),
            "═══════════════════════════════════════",
            "Last Login: " + (usuario.getUltimaSesion() != null ? sdf.format(usuario.getUltimaSesion()) : "Never"),
            "Registered: " + (usuario.getFechaRegistro() != null ? sdf.format(usuario.getFechaRegistro()) : "Unknown")
        };
        
        String avatarPath = "/res/" + usuario.getAvatar();
        avatarActual = loadImageFromResource(avatarPath);
        necesitaActualizar = false;
    }
    
    private String obtenerColorAvatarActual() {
        Usuario usuario = sessionManager.getUsuarioActual();
        return usuario != null ? usuario.getAvatarColorHex() : "#a2b794";
    }
    
    @Override
    public void update() {
        if (sessionManager.getUsuarioActual() == null) return;
        if (!visible) return;
        
        btnVolver.update();
        btnCerrarSesion.update();
        btnEliminarCuenta.update();
        btnCambiarAvatar.update();
        btnConfigAudio.update();
        btnAmigos.update();
        btnStats.update();
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (sessionManager.getUsuarioActual() == null) return;
        if (!visible) return;
        
        if (necesitaActualizar) actualizarInfo();
        
        // Fondo
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        // Avatar
        if (avatarActual != null) {
            g.drawImage(avatarActual, 80, 120, 140, 140, null);
            g.setColor(Color.decode(obtenerColorAvatarActual()));
            g.drawRoundRect(80, 120, 140, 140, 15, 15);
        }
        
        // Marco decorativo del avatar
        g.setColor(new Color(255, 200, 100));
        g.drawRoundRect(78, 118, 144, 144, 15, 15);
        
        // Información del perfil
        g.setFont(g.getFont().deriveFont(14f));
        
        int y = 140;
        for (String linea : infoLines) {
            if (linea.startsWith("═══")) {
                g.setColor(new Color(255, 200, 100));
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(linea, 260, y);
            y += 22;
        }
        
        // Botones
        btnVolver.draw(g);
        btnCerrarSesion.draw(g);
        btnEliminarCuenta.draw(g);
        btnCambiarAvatar.draw(g);
        btnConfigAudio.draw(g);
        btnAmigos.draw(g);
        btnStats.draw(g);
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.PERFIL);
        if (visible) {
            necesitaActualizar = true;
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    

}