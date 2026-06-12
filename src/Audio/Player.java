
package Audio;


import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;

public class Player {
    private static final String ASSETS_DIR = "/res/";
    
    private MediaPlayer musicaPlayer;
    private Config config;
    
    public Player(Config config) {
        this.config = config;
    }
    
    public void reproducirSFX(String archivo) {
        if (!config.isSfxActivo() || config.getVolumenSFX() <= 0) return;
        
        Platform.runLater(() -> {
            try {
                MediaPlayer sfx = new MediaPlayer(new Media(new File(ASSETS_DIR + archivo).toURI().toString()));
                sfx.setVolume(config.getVolumenSFX() / 100.0);
                sfx.setOnEndOfMedia(sfx::dispose);
                sfx.play();
            } catch (Exception e) {
                System.err.println("Error SFX: " + e.getMessage());
            }
        });
    }
    
    public void iniciarMusica() {
        if (!config.isMusicaActiva() || config.getVolumenMusica() <= 0) return;
        
        Platform.runLater(() -> {
            if (musicaPlayer != null) {
                musicaPlayer.play();
                return;
            }
            
            try {
                Media media = new Media(new File(ASSETS_DIR + "fondo.mp3").toURI().toString());
                musicaPlayer = new MediaPlayer(media);
                musicaPlayer.setVolume(config.getVolumenMusica() / 100.0);
                musicaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                
                double pos = config.getPosicionMusica();
                musicaPlayer.setOnReady(() -> {
                    if (pos > 0) musicaPlayer.seek(Duration.seconds(pos));
                    musicaPlayer.play();
                });
            } catch (Exception e) {
                System.err.println("Error música: " + e.getMessage());
            }
        });
    }
    
    public void detenerMusica() {
        guardarPosicion();
        Platform.runLater(() -> {
            if (musicaPlayer != null) {
                musicaPlayer.stop();
                musicaPlayer.dispose();
                musicaPlayer = null;
            }
        });
    }
    
    public void actualizarVolumenMusica() {
        Platform.runLater(() -> {
            if (musicaPlayer != null) {
                musicaPlayer.setVolume(config.getVolumenMusica() / 100.0);
                musicaPlayer.setMute(!config.isMusicaActiva());
            }
        });
    }
    
    public void guardarPosicion() {
        Platform.runLater(() -> {
            if (musicaPlayer != null) {
                config.setPosicionMusica(musicaPlayer.getCurrentTime().toSeconds());
            }
        });
    }
}
