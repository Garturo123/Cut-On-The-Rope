package ctr.entity;

import Audio.Manager;
import Usuarios.Menu;
import Usuarios.SessionManager;
import ctr.Entity;
import ctr.I18n;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;

public class AudioConfigEntity extends Entity {
    
    private Button btnVolver;
    private Button btnSFXUp;
    private Button btnSFXDown;
    private Button btnMusicUp;
    private Button btnMusicDown;
    private Button btnMute;
    
    private final Manager audioManager;
    private final Menu menus;
    
    private final SessionManager session;

    private int sfxVolume;
    private int musicVolume;
    private boolean mute;
    private int contadorMensaje = 0;
    private boolean necesitaGuardar = false;
    
    public AudioConfigEntity(Scene scene, Manager audioManager, Menu menus,SessionManager session ) {
        super(scene);
        this.audioManager = audioManager;
        this.menus = menus;
        this.session = session;
        
        // Inicializar valores desde el Manager
        actualizarValores();
        
        btnVolver = new Button(scene, I18n.t("back"), 50, 28, 415, 610);
        btnSFXDown = new Button(scene, "-", 20, 28, 210, 220);
        btnSFXUp = new Button(scene, "+", 20, 28, 700, 220);
        btnMusicDown = new Button(scene, "-", 20, 28, 210, 370);
        btnMusicUp = new Button(scene, "+", 20, 28, 700, 370);
        btnMute = new Button(scene, mute ? I18n.t("unmute") : I18n.t("mute"), 55, 28, 415, 515);
        
        btnVolver.setListener(() -> {
            guardarCambios();
            scene.cambiarAState(GameState.SETTINGS);
        });
        
        btnSFXUp.setListener(() -> {
            sfxVolume = Math.min(100, sfxVolume + 10);
            audioManager.setVolumenSFX(sfxVolume);
            necesitaGuardar = true;
            contadorMensaje = 60;
        });
        
        btnSFXDown.setListener(() -> {
            sfxVolume = Math.max(0, sfxVolume - 10);
            audioManager.setVolumenSFX(sfxVolume);
            necesitaGuardar = true;
            contadorMensaje = 60;
        });
        
        btnMusicUp.setListener(() -> {
            musicVolume = Math.min(100, musicVolume + 10);
            audioManager.setVolumenMusica(musicVolume);
            necesitaGuardar = true;
            contadorMensaje = 60;
        });
        
        btnMusicDown.setListener(() -> {
            musicVolume = Math.max(0, musicVolume - 10);
            audioManager.setVolumenMusica(musicVolume);
            necesitaGuardar = true;
            contadorMensaje = 60;
        });
        
        btnMute.setListener(() -> {
            audioManager.alternarMuteGeneral();
            mute = audioManager.isMute();
            btnMute.setText(mute ? I18n.t("unmute") : I18n.t("mute"));
            contadorMensaje = 60;
            necesitaGuardar = true;
        });
    }
    
    private void actualizarValores() {
        if (audioManager != null) {
            sfxVolume = audioManager.getVolumenSFX();
            musicVolume = audioManager.getVolumenMusica();
            mute = audioManager.isMute();
        } else {
            sfxVolume = 80;
            musicVolume = 60;
            mute = false;
        }
    }
    
    private void guardarCambios() {
        if (necesitaGuardar && menus != null && session.getUsuarioActual() != null) {
            // Persistir en el usuario
            session.getUsuarioActual().actualizarConfigAudio(
                sfxVolume,
                musicVolume,
                !mute,  // sfxActivo
                !mute,  // musicaActiva
                0       // posición música
            );
            necesitaGuardar = false;
        }
    }
    
    @Override
    public void update() {
        if (!visible) return;
        btnVolver.update();
        btnSFXUp.update();
        btnSFXDown.update();
        btnMusicUp.update();
        btnMusicDown.update();
        btnMute.update();
        
        if (contadorMensaje > 0) {
            contadorMensaje--;
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);
        
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(32f));
        g.drawString(I18n.t("audio_settings"), 380, 100);
        
        // SFX Volume
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString(I18n.t("sfx_volume"), 420, 195);
        g.drawString(sfxVolume + "%", 665, 195);
        
        // Barra de volumen SFX
        g.setColor(Color.GRAY);
        g.fillRect(410, 245, 240, 15);
        g.setColor(new Color(100, 200, 100));
        g.fillRect(410, 245, (int)(sfxVolume * 2.4), 15);
        
        g.setColor(Color.WHITE);
        // Music Volume
        g.drawString(I18n.t("music_volume"), 420, 345);
        g.drawString(musicVolume + "%", 665, 345);
        
        // Barra de volumen Música
        g.setColor(Color.GRAY);
        g.fillRect(410, 395, 240, 15);
        g.setColor(new Color(100, 150, 255));
        g.fillRect(410, 395, (int)(musicVolume * 2.4), 15);
        
        // Indicador de mute
        if (mute) {
            g.setColor(Color.RED);
            g.drawString("MUTED", 455, 500);
        }
        
        btnSFXDown.draw(g);
        btnSFXUp.draw(g);
        btnMusicDown.draw(g);
        btnMusicUp.draw(g);
        btnMute.draw(g);
        btnVolver.draw(g);
        
        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(14f));
            g.drawString(I18n.t("audio_saved"), 420, 580);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.AUDIO_CONFIG);
        if (visible) {
            actualizarValores();
            refreshText();
            necesitaGuardar = false;
        }
    }

    private void refreshText() {
        btnVolver.setText(I18n.t("back"));
        btnMute.setText(mute ? I18n.t("unmute") : I18n.t("mute"));
    }
    
    @Override
    protected void updateLevelCleared() {}
    
}
