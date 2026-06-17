package Audio;

import ctr.ResourceLoader;
import javax.sound.sampled.*;
import java.io.InputStream;

public class Player {
    private static final String ASSETS_DIR = "/res/";
    
    private Config config;
    private Clip musicaPlayer;
    public Player(Config config) {
        this.config = config;
    }
    
    public void reproducirSFX(String archivo) {

        if (!config.isSfxActivo() || config.getVolumenSFX() <= 0) {
            return;
        }

        try {

            AudioInputStream ais =
                AudioSystem.getAudioInputStream(
                    openAudio(ASSETS_DIR + archivo)
                );

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            FloatControl gain =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float volumen = config.getVolumenSFX() / 100f;

            if (volumen == 0) {
                gain.setValue(gain.getMinimum());
            } else {
                gain.setValue(
                    (float)(20 * Math.log10(volumen))
                );
            }

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void iniciarMusica() {
        if (!config.isMusicaActiva() || config.getVolumenMusica() <= 0) {
            return;
        }

        if (musicaPlayer != null) {
            musicaPlayer.start();
            return;
        }

        try {

            AudioInputStream ais =
                AudioSystem.getAudioInputStream(
                    openAudio(ASSETS_DIR + "fondo.wav")
                );

            musicaPlayer = AudioSystem.getClip();

            musicaPlayer.open(ais);

            actualizarVolumenMusica();

            musicaPlayer.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void detenerMusica() {

        guardarPosicion();

        if (musicaPlayer != null) {
            musicaPlayer.stop();
            musicaPlayer.close();
            musicaPlayer = null;
        }
    }
    
    public void actualizarVolumenMusica() {

    if (musicaPlayer == null) {
        return;
    }

    try {

        FloatControl gain =
            (FloatControl) musicaPlayer.getControl(
                FloatControl.Type.MASTER_GAIN
            );

        float volumen = config.getVolumenMusica() / 100f;

        if (volumen == 0) {
            gain.setValue(gain.getMinimum());
        } else {
            gain.setValue(
                (float)(20 * Math.log10(volumen))
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    public void guardarPosicion() {
        if (musicaPlayer == null) {
            return;
        }
        config.setPosicionMusica(
            musicaPlayer.getMicrosecondPosition() / 1_000_000.0
        );
    }

    private InputStream openAudio(String resource) throws Exception {
        return ResourceLoader.open(resource);
    }
}
