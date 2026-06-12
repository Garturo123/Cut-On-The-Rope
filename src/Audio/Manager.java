package Audio;

import Usuarios.Menu;
import Usuarios.Usuario;
import javafx.embed.swing.JFXPanel;
import javax.swing.*;

public class Manager {
    private final Config config;
    private final Player player;
    private final Menu menus;
    
    static { new JFXPanel(); }
    
    public Manager(Menu menus) {
        this.menus = menus;
        this.config = new Config();
        this.player = new Player(config);
        cargarConfiguracion();
    }
    
    private void cargarConfiguracion() {
        if (menus != null && menus.getUsuarioActual() != null) {
            Usuario u = menus.getUsuarioActual();
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
        if (menus != null) {
            menus.actualizarConfigAudio(
                config.getVolumenSFX(),
                config.getVolumenMusica(),
                config.isSfxActivo(),
                config.isMusicaActiva(),
                config.getPosicionMusica()
            );
        }
    }
}