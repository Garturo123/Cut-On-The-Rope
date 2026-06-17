package Audio;

import ctr.ResourceLoader;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Player {
    private static final String ASSETS_DIR = "/res/";

    private Config config;
    private Clip musicaPlayer;

    public Player(Config config) {
        this.config = config;
    }

    /**
     * Reproduce un SFX de forma no bloqueante.
     * Carga el WAV en memoria antes de abrirlo para evitar problemas
     * de mark/reset en el AudioInputStream, y registra un LineListener
     * para cerrar el Clip automáticamente al terminar (evita fuga de líneas).
     */
    public void reproducirSFX(String archivo) {
        if (!config.isSfxActivo() || config.getVolumenSFX() <= 0) {
            return;
        }

        try {
            // 1. Leer todo el archivo en memoria para que mark/reset funcione siempre
            byte[] data = readAllBytes(ASSETS_DIR + archivo);
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new ByteArrayInputStream(data));

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            ais.close();

            // 2. Ajustar volumen
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumen = config.getVolumenSFX() / 100f;
                if (volumen <= 0f) {
                    gain.setValue(gain.getMinimum());
                } else {
                    gain.setValue((float) (20.0 * Math.log10(volumen)));
                }
            }

            // 3. Cerrar el Clip automáticamente cuando termina de reproducirse
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });

            clip.start();

        } catch (Exception e) {
            System.err.println("[Audio] No se pudo reproducir SFX '" + archivo + "': " + e.getMessage());
        }
    }

    public void iniciarMusica() {
        if (!config.isMusicaActiva() || config.getVolumenMusica() <= 0) {
            return;
        }

        if (musicaPlayer != null) {
            if (!musicaPlayer.isRunning()) {
                musicaPlayer.start();
            }
            return;
        }

        try {
            byte[] data = readAllBytes(ASSETS_DIR + "fondo.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new ByteArrayInputStream(data));

            musicaPlayer = AudioSystem.getClip();
            musicaPlayer.open(ais);
            ais.close();

            actualizarVolumenMusica();
            musicaPlayer.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.err.println("[Audio] No se pudo iniciar musica: " + e.getMessage());
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
            if (musicaPlayer.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) musicaPlayer.getControl(FloatControl.Type.MASTER_GAIN);
                float volumen = config.getVolumenMusica() / 100f;
                if (volumen <= 0f) {
                    gain.setValue(gain.getMinimum());
                } else {
                    gain.setValue((float) (20.0 * Math.log10(volumen)));
                }
            }
        } catch (Exception e) {
            System.err.println("[Audio] Error al actualizar volumen: " + e.getMessage());
        }
    }

    public void guardarPosicion() {
        if (musicaPlayer == null) {
            return;
        }
        config.setPosicionMusica(musicaPlayer.getMicrosecondPosition() / 1_000_000.0);
    }

    /** Lee un recurso completo en un arreglo de bytes. */
    private byte[] readAllBytes(String resource) throws Exception {
        try (InputStream in = ResourceLoader.open(resource)) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] block = new byte[4096];
            int n;
            while ((n = in.read(block)) != -1) {
                buf.write(block, 0, n);
            }
            return buf.toByteArray();
        }
    }
}
