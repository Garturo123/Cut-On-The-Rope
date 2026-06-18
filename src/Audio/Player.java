package Audio;

import ctr.ResourceLoader;
import javax.sound.sampled.*;
import java.io.InputStream;

public class Player {
    private static final String ASSETS_DIR = "/res/";
    private static final String DEFAULT_MUSIC = "ctr_MainTheme.mp3";
    
    private Config config;
    private Clip musicaClip;
    private String musicaActual;
    private String musicaSolicitada = DEFAULT_MUSIC;
    private boolean cargandoMusica;
    private boolean reproducirMusicaCuandoCargue;

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
            ais.close();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });

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
        if (!config.isMusicaActiva() || config.getVolumenMusica() <= 0) {
            if (musicaClip != null) {
                actualizarVolumenMusica();
            }
            return;
        }

        if (archivo.equals(musicaActual) && musicaClip != null) {
            actualizarVolumenMusica();
            if (musicaClip != null && !musicaClip.isRunning())
                musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
            return;
        }

        reproducirMusicaCuandoCargue = true;
        prepararMusica(archivo);
    }
    
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
    }

    private InputStream openAudio(String resource) throws Exception {
        return ResourceLoader.open(resource);
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
