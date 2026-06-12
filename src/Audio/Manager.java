package Audio;

import Usuarios.Menu;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import javafx.embed.swing.JFXPanel;
import javax.swing.*;

public class Manager {
    private final Config config;
    private final Player player;
    private final Menu menus;
    private final SessionManager session;  // Ahora se inicializa

    static { 
        try {
            new JFXPanel(); 
        } catch (Exception e) {
            System.err.println("JavaFX no disponible: " + e.getMessage());
        }
    }
    
    // ✅ CONSTRUCTOR CORREGIDO
    public Manager(Menu menus, SessionManager session) {
        this.menus = menus;
        this.session = session;  // ← Inicialización agregada
        this.config = new Config();
        this.player = new Player(config);
        cargarConfiguracion();
    }
    
    private void cargarConfiguracion() {
        // ✅ Validación completa contra null
        if (menus != null && session != null && session.getUsuarioActual() != null) {
            Usuario u = session.getUsuarioActual();
            config.setVolumenSFX(u.getVolumenSFX());
            config.setVolumenMusica(u.getVolumenMusica());
            config.setPosicionMusica(u.getPosicionMusicaSegundos());
        }
    }
    
    public void reproducirSFX(String archivo) { player.reproducirSFX(archivo); }
    public void iniciarMusicaPartida() { player.iniciarMusica(); }
    public void detenerMusica() { player.detenerMusica(); guardarConfig(); }
    
    public void alternarMuteGeneral() {
        config.toggleMute();
        player.actualizarVolumenMusica();
        guardarConfig();
    }
    
    public void mostrarControlSFX(JFrame parent) {
        VolumeDialog.mostrar(parent, "SFX Volume", "SFX Volume", config.getVolumenSFX(), nuevoVol -> {
            config.setVolumenSFX(nuevoVol);
            guardarConfig();
        });
    }
    
    public void mostrarControlMusica(JFrame parent) {
        VolumeDialog.mostrar(parent, "Music Volume", "Music Volume", config.getVolumenMusica(), nuevoVol -> {
            config.setVolumenMusica(nuevoVol);
            player.actualizarVolumenMusica();
            guardarConfig();
        });
    }
    
    private void guardarConfig() {
        player.guardarPosicion();
        if (session.getUsuarioActual() != null) {
           session.getUsuarioActual().actualizarConfigAudio(
                config.getVolumenSFX(),
                config.getVolumenMusica(),
                config.isSfxActivo(),
                config.isMusicaActiva(),
                config.getPosicionMusica()
            );
        }
    }
    // En Audio/Manager.java - añade estos métodos:

    public void setVolumenSFX(int volumen) {
        config.setVolumenSFX(volumen);
        player.actualizarVolumenMusica(); // Si Player tiene este método
        guardarConfig();
    }

    public void setVolumenMusica(int volumen) {
        config.setVolumenMusica(volumen);
        player.actualizarVolumenMusica();
        guardarConfig();
    }

    public int getVolumenSFX() {
        return config.getVolumenSFX();
    }

    public int getVolumenMusica() {
        return config.getVolumenMusica();
    }

    public boolean isMute() {
        return config.isMute;
    }
}