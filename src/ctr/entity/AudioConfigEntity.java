package ctr.entity;

import Audio.Manager;
import Usuarios.Menu;
import Usuarios.SessionManager;
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
        
        btnVolver = new Button(scene, "Back", 50, 28, 50, 550);
        btnSFXUp = new Button(scene, "+", 20, 28, 500, 220);
        btnSFXDown = new Button(scene, "-", 20, 28, 400, 220);
        btnMusicUp = new Button(scene, "+", 20, 28, 500, 320);
        btnMusicDown = new Button(scene, "-", 20, 28, 400, 320);
        btnMute = new Button(scene, mute ? "Unmute" : "Mute", 55, 28, 340, 420);
        
        btnVolver.setListener(() -> {
            guardarCambios();
            scene.cambiarAState(GameState.PERFIL);
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
            btnMute.setText(mute ? "Unmute" : "Mute");
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
        g.drawString("AUDIO SETTINGS", 280, 100);
        
        // SFX Volume
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString("SFX Volume:", 250, 240);
        g.drawString(sfxVolume + "%", 560, 240);
        
        // Barra de volumen SFX
        g.setColor(Color.GRAY);
        g.fillRect(380, 215, 200, 15);
        g.setColor(new Color(100, 200, 100));
        g.fillRect(380, 215, sfxVolume * 2, 15);
        
        // Music Volume
        g.drawString("Music Volume:", 250, 340);
        g.drawString(musicVolume + "%", 560, 340);
        
        // Barra de volumen Música
        g.setColor(Color.GRAY);
        g.fillRect(380, 315, 200, 15);
        g.setColor(new Color(100, 150, 255));
        g.fillRect(380, 315, musicVolume * 2, 15);
        
        // Indicador de mute
        if (mute) {
            g.setColor(Color.RED);
            g.drawString("🔇 MUTED", 380, 460);
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
            g.drawString("Audio settings saved!", 330, 500);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.AUDIO_CONFIG);
        if (visible) {
            actualizarValores();
            btnMute.setText(mute ? "Unmute" : "Mute");
            necesitaGuardar = false;
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}