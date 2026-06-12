package ctr.entity;

import Audio.Manager;
import Usuarios.Menu;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PerfilEntity extends Entity {
    
    private Button btnVolver;
    private Button btnCerrarSesion;
    private Button btnEliminarCuenta;
    private Button btnCambiarAvatar;
    private Button btnConfigAudio;
    private Button btnAmigos;
    private Button btnStats;
    
    private transient Menu menus;
    private transient Manager audioManager;
    
    private BufferedImage avatarActual;
    private String[] infoLines;
    private boolean necesitaActualizar = true;
    
    public PerfilEntity(Scene scene, Menu menus, Manager audioManager) {
        super(scene);
        this.menus = menus;
        this.audioManager = audioManager;
        
        int xCol1 = 100;
        int xCol2 = 350;
        int yStart = 500;
        
        btnVolver = new Button(scene, "Back", 50, 28, 50, 550);
        btnCerrarSesion = new Button(scene, "Logout", 60, 28, xCol1, yStart);
        btnEliminarCuenta = new Button(scene, "Delete", 55, 28, xCol1 + 130, yStart);
        btnCambiarAvatar = new Button(scene, "Avatar", 60, 28, xCol2, yStart);
        btnConfigAudio = new Button(scene, "Audio", 55, 28, xCol2 + 110, yStart);
        btnAmigos = new Button(scene, "Friends", 60, 28, xCol2 + 220, yStart);
        btnStats = new Button(scene, "Stats", 55, 28, xCol2 + 330, yStart);
        
        btnVolver.setListener(() -> scene.cambiarAState(GameState.LEVEL_SELECT));
        btnCerrarSesion.setListener(() -> {
            menus.logout();
            scene.cambiarAState(GameState.MENU_PRINCIPAL);
        });
        btnEliminarCuenta.setListener(() -> {
            menus.eliminarCuentaActual();
            scene.cambiarAState(GameState.MENU_PRINCIPAL);
        });
        btnCambiarAvatar.setListener(() -> scene.cambiarAState(GameState.AVATAR_SELECTOR));
        btnConfigAudio.setListener(() -> scene.cambiarAState(GameState.AUDIO_CONFIG));
        btnAmigos.setListener(() -> scene.cambiarAState(GameState.AMIGOS_LIST));
        btnStats.setListener(() -> scene.cambiarAState(GameState.STATS));
    }
    
    private void actualizarInfo() {
        if (menus.getUsuarioActual() == null) return;
        
        infoLines = new String[] {
            "═══════════════════════════════════════",
            "USER PROFILE",
            "═══════════════════════════════════════",
            "Username: " + menus.obtenerUsernamePerfil(),
            "Name: " + menus.obtenerNombrePerfil(),
            "Status: " + menus.obtenerStatusPerfil(),
            "═══════════════════════════════════════",
            "STATISTICS",
            "═══════════════════════════════════════",
            "Total Score: " + menus.obtenerScorePerfil(),
            "Levels Completed: " + menus.obtenerNivelesCompletadosPerfil(),
            "Challenges Won: " + menus.obtenerRetosGanadosPerfil(),
            "Friends: " + menus.obtenerCantidadAmigosPerfil(),
            "═══════════════════════════════════════",
            "Last Login: " + menus.obtenerUltimoLoginPerfil(),
            "Registered: " + menus.obtenerFechaRegistroPerfil()
        };
        
        String avatarPath = menus.obtenerAvatarPathPerfil();
        avatarActual = loadImageFromResource(avatarPath);
        necesitaActualizar = false;
    }
    
    @Override
    public void update() {
        if (menus.getUsuarioActual() == null) return;
        
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
        if (menus.getUsuarioActual() == null) return;
        
        if (necesitaActualizar) actualizarInfo();
        
        // Fondo
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        // Avatar
        if (avatarActual != null) {
            g.drawImage(avatarActual, 80, 120, 140, 140, null);
            g.setColor(Color.decode(menus.obtenerColorAvatarActual()));
            g.drawRoundRect(80, 120, 140, 140, 15, 15);
        }
        
        // Marco decorativo del avatar
        g.setColor(new Color(255, 200, 100));
        g.drawRoundRect(78, 118, 144, 144, 15, 15);
        
        // Información del perfil
        g.setColor(Color.WHITE);
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
    
    @Override
    protected void updateFixedLevelCleared() {}
}
