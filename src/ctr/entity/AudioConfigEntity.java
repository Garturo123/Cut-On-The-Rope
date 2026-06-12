package ctr.entity;

import Audio.Config;
import Audio.Manager;
import Usuarios.Menu;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
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
    
    private transient Manager audioManager;
    private transient Menu menus;
    
    private int sfxVolume;
    private int musicVolume;
    private boolean mute;
    private int contadorMensaje = 0;
    
    public AudioConfigEntity(Scene scene, Manager audioManager, Menu menus) {
        super(scene);
        this.audioManager = audioManager;
        this.menus = menus;
        
        btnVolver = new Button(scene, "Back", 50, 28, 50, 550);
        btnSFXUp = new Button(scene, "+", 20, 28, 500, 220);
        btnSFXDown = new Button(scene, "-", 20, 28, 400, 220);
        btnMusicUp = new Button(scene, "+", 20, 28, 500, 320);
        btnMusicDown = new Button(scene, "-", 20, 28, 400, 320);
        btnMute = new Button(scene, "Mute", 55, 28, 340, 420);
        
        btnVolver.setListener(() -> scene.cambiarAState(GameState.PERFIL));
        btnSFXUp.setListener(() -> {
            sfxVolume = Math.min(100, sfxVolume + 10);
            Config.setVolumenSFX(sfxVolume);
        });
        btnSFXDown.setListener(() -> {
            sfxVolume = Math.max(0, sfxVolume - 10);
            audioManager.setVolumenSFX(sfxVolume);
        });
        btnMusicUp.setListener(() -> {
            musicVolume = Math.min(100, musicVolume + 10);
            audioManager.setVolumenMusica(musicVolume);
        });
        btnMusicDown.setListener(() -> {
            musicVolume = Math.max(0, musicVolume - 10);
            audioManager.setVolumenMusica(musicVolume);
        });
        btnMute.setListener(() -> {
            audioManager.alternarMuteGeneral();
            actualizarValores();
            contadorMensaje = 60;
        });
    }
    
    private void actualizarValores() {
        if (menus != null && menus.getUsuarioActual() != null) {
            sfxVolume = menus.getUsuarioActual().getVolumenSFX();
            musicVolume = menus.getUsuarioActual().getVolumenMusica();
        }
    }
    
    @Override
    public void update() {
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
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(32f));
        g.drawString("AUDIO SETTINGS", 300, 100);
        
        // SFX Volume
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString("SFX Volume:", 250, 240);
        g.drawString(sfxVolume + "%", 550, 240);
        
        // Barra de volumen SFX
        g.setColor(Color.GRAY);
        g.fillRect(380, 215, 200, 15);
        g.setColor(new Color(100, 200, 100));
        g.fillRect(380, 215, sfxVolume * 2, 15);
        
        // Music Volume
        g.drawString("Music Volume:", 250, 340);
        g.drawString(musicVolume + "%", 550, 340);
        
        // Barra de volumen Música
        g.setColor(Color.GRAY);
        g.fillRect(380, 315, 200, 15);
        g.setColor(new Color(100, 150, 255));
        g.fillRect(380, 315, musicVolume * 2, 15);
        
        btnSFXDown.draw(g);
        btnSFXUp.draw(g);
        btnMusicDown.draw(g);
        btnMusicUp.draw(g);
        btnMute.draw(g);
        btnVolver.draw(g);
        
        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(14f));
            g.drawString("Audio settings saved!", 330, 500);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.AUDIO_CONFIG);
        if (visible) {
            actualizarValores();
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}