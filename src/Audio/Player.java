package Audio;

import ctr.ResourceLoader;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Player {
    private static final String ASSETS_DIR = "/res/";
<<<<<<< HEAD

    private Config config;
    private Clip musicaPlayer;
=======
    private static final String DEFAULT_MUSIC = "ctr_MainTheme.mp3";
    
    private Config config;
    private Clip musicaClip;
    private String musicaActual;
    private String musicaSolicitada = DEFAULT_MUSIC;
    private boolean cargandoMusica;
    private boolean reproducirMusicaCuandoCargue;
>>>>>>> f18e129fc6bd45191c5edb08507bce6b43471860

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
<<<<<<< HEAD
=======
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });
>>>>>>> f18e129fc6bd45191c5edb08507bce6b43471860

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
<<<<<<< HEAD

    public void iniciarMusica() {
=======
    
    public synchronized void prepararMusica() {
        prepararMusica(DEFAULT_MUSIC);
    }

    public synchronized void prepararMusica(String archivo) {
        if (archivo == null || archivo.trim().isEmpty()) {
            archivo = DEFAULT_MUSIC;
        }
        archivo = resolverArchivoMusica(archivo);
        if (archivo == null) {
            cerrarMusicaActual();
            cargandoMusica = false;
            reproducirMusicaCuandoCargue = false;
            return;
        }
        if (archivo.equals(musicaActual) && musicaClip != null) {
            return;
        }
        if (archivo.equals(musicaSolicitada) && cargandoMusica) {
            return;
        }

        cerrarMusicaActual();
        musicaSolicitada = archivo;

        cargandoMusica = true;
        final String archivoFinal = archivo;
        Thread loader = new Thread(() -> {
            Clip clip = null;
            try {
                AudioInputStream ais =
                    AudioSystem.getAudioInputStream(
                        openAudio(ASSETS_DIR + archivoFinal)
                    );

                clip = AudioSystem.getClip();
                clip.open(ais);
                ais.close();

                synchronized (Player.this) {
                    if (!archivoFinal.equals(musicaSolicitada)) {
                        clip.close();
                        cargandoMusica = false;
                        return;
                    }
                    musicaClip = clip;
                    musicaActual = archivoFinal;
                    clip = null;
                    cargandoMusica = false;
                    restaurarPosicionMusica();
                    actualizarVolumenMusica();
                    if (reproducirMusicaCuandoCargue) {
                        reproducirMusicaCuandoCargue = false;
                        iniciarMusica(archivoFinal);
                    }
                }
            } catch (Exception e) {
                synchronized (Player.this) {
                    cargandoMusica = false;
                    reproducirMusicaCuandoCargue = false;
                }
                if (clip != null) {
                    clip.close();
                }
                e.printStackTrace();
            }
        }, "ctr-music-loader");
        loader.setDaemon(true);
        loader.start();
    }

    public synchronized void iniciarMusica() {
        iniciarMusica(musicaSolicitada == null ? DEFAULT_MUSIC : musicaSolicitada);
    }

    public synchronized void iniciarMusica(String archivo) {
        if (archivo == null || archivo.trim().isEmpty()) {
            archivo = DEFAULT_MUSIC;
        }
        archivo = resolverArchivoMusica(archivo);
        if (archivo == null) {
            cerrarMusicaActual();
            reproducirMusicaCuandoCargue = false;
            return;
        }
>>>>>>> f18e129fc6bd45191c5edb08507bce6b43471860
        if (!config.isMusicaActiva() || config.getVolumenMusica() <= 0) {
            if (musicaClip != null) {
                actualizarVolumenMusica();
            }
            return;
        }

<<<<<<< HEAD
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
=======
        if (archivo.equals(musicaActual) && musicaClip != null) {
            actualizarVolumenMusica();
            if (musicaClip != null && !musicaClip.isRunning())
                musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
            return;
>>>>>>> f18e129fc6bd45191c5edb08507bce6b43471860
        }

        reproducirMusicaCuandoCargue = true;
        prepararMusica(archivo);
    }
<<<<<<< HEAD

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
=======
    
    public synchronized void detenerMusica() {

        guardarPosicion();
        cerrarMusicaActual();
    }
    
    public synchronized void actualizarVolumenMusica() {

    if (musicaClip == null) {
        return;
    }

    try {
        float volumen = config.isMusicaActiva() ? config.getVolumenMusica() / 100f : 0f;

        if (musicaClip != null) {
            FloatControl gain =
                (FloatControl) musicaClip.getControl(
                    FloatControl.Type.MASTER_GAIN
                );

            if (volumen == 0) {
                gain.setValue(gain.getMinimum());
            } else {
                gain.setValue(
                    (float)(20 * Math.log10(volumen))
                );
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    public synchronized void guardarPosicion() {
        if (musicaClip != null) {
            config.setPosicionMusica(
                musicaClip.getMicrosecondPosition() / 1_000_000.0
            );
        }
>>>>>>> f18e129fc6bd45191c5edb08507bce6b43471860
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

    private void restaurarPosicionMusica() {
        if (musicaClip == null || config.getPosicionMusica() <= 0) {
            return;
        }
        long posicion = (long) (config.getPosicionMusica() * 1_000_000.0);
        long max = Math.max(0, musicaClip.getMicrosecondLength() - 1);
        musicaClip.setMicrosecondPosition(Math.min(posicion, max));
    }

    private void cerrarMusicaActual() {
        if (musicaClip != null) {
            musicaClip.stop();
            musicaClip.close();
            musicaClip = null;
        }
        musicaActual = null;
    }

    private String resolverArchivoMusica(String archivo) {
        String wav = archivo.replaceAll("(?i)\\.mp3$", ".wav");
        if (!wav.equals(archivo) && existeRecurso(ASSETS_DIR + wav)) {
            return wav;
        }
        if (esFormatoSoportadoPorClip(archivo) && existeRecurso(ASSETS_DIR + archivo)) {
            return archivo;
        }
        return null;
    }

    private boolean esFormatoSoportadoPorClip(String archivo) {
        String lower = archivo.toLowerCase();
        return lower.endsWith(".wav") || lower.endsWith(".aif") || lower.endsWith(".aiff") || lower.endsWith(".au");
    }

    private boolean existeRecurso(String resource) {
        try (InputStream ignored = openAudio(resource)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
