package ctr.entity;

import Audio.Manager;
import ctr.Entity;
import ctr.Scene;
import ctr.model.AirCushion;
import ctr.model.AirCushionListener;
import ctr.model.CandyListener;
import ctr.model.ModelListener;
import ctr.model.PetListener;
import ctr.model.Star;
import ctr.model.StarListener;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Entidad de sonidos del juego.
 *
 * Mapa de sonidos:
 *   - 1ª estrella tocada       → estrellas1.wav
 *   - 2ª estrella tocada       → estrella2.wav
 *   - 3ª estrella tocada       → estrella3.wav
 *   - bolsa de aire presionada → bolsa_de_aire.wav  (cada presión)
 *   - alien come el caramelo   → comiendo.wav
 *   - caramelo no llega        → no_lo_comio.wav
 *
 * Rutas de fallo cubiertas:
 *   A) Caramelo cae / sale volando → Candy.update() → model.levelFailured()
 *                                    → ModelListener.onFailure()
 *   B) Caramelo toca pinchos       → Candy.destroy() → CandyListener.onCandyDestroyed()
 *                                    → model.levelFailured() → ModelListener.onFailure()
 *   En ambos casos se reproduce no_lo_comio.wav exactamente una vez.
 */
public class GameSoundEntity extends Entity
        implements StarListener, PetListener, ModelListener, AirCushionListener, CandyListener {

    private static final String SFX_ESTRELLA_1  = "estrellas1.wav";
    private static final String SFX_ESTRELLA_2  = "estrella2.wav";
    private static final String SFX_ESTRELLA_3  = "estrella3.wav";
    private static final String SFX_BOLSA_AIRE  = "bolsa_de_aire.wav";
    private static final String SFX_COMIENDO    = "comiendo.wav";
    private static final String SFX_NO_LO_COMIO = "no_lo_comio.wav";

    /** Índice de la siguiente estrella esperada (0 = primera, 1 = segunda, 2 = tercera). */
    private int nextStarIndex;

    /** Garantiza que no_lo_comio.wav solo suene una vez por intento. */
    private boolean failSoundPlayed;

    /** true después de que onCandyEaten se disparó. */
    private boolean candyEaten;

    private final Manager audioManager;

    // ------------------------------------------------------------------ //
    //  Constructor
    // ------------------------------------------------------------------ //
    public GameSoundEntity(Scene scene) {
        super(scene);
        this.audioManager = scene.getAudioManager();

        // Modelo → onFailure / onLevelCleared
        scene.getModel().addListener(this);

        // Pet → onCandyEaten / onCandyClose / onCandyEscaped
        if (scene.getModel().getPet() != null) {
            scene.getModel().getPet().addListener(this);
        }

        // Candy → onCandyDestroyed  (pinchos u otra destrucción explícita)
        if (scene.getModel().getCandy() != null) {
            scene.getModel().getCandy().addListener(this);
        }

        // Estrellas → onStarCaught, en orden de creación del nivel
        List<Star> stars = scene.getModel().getStars();
        for (Star star : stars) {
            star.addListener(this);
        }

        // Bolsas de aire → onAirCushionFire
        for (AirCushion ac : scene.getModel().getAirCushions()) {
            ac.addListener(this);
        }
    }

    // ------------------------------------------------------------------ //
    //  Entity — sin dibujo
    // ------------------------------------------------------------------ //
    @Override
    public void draw(Graphics2D g) { /* nada que dibujar */ }

    @Override
    public void gameStateChanged(Scene.GameState newGameState) {
        if (newGameState == Scene.GameState.PLAYING) {
            nextStarIndex   = 0;
            failSoundPlayed = false;
            candyEaten      = false;
        }
    }

    // ------------------------------------------------------------------ //
    //  StarListener
    // ------------------------------------------------------------------ //
    @Override
    public void onStarCaught() {
        switch (nextStarIndex) {
            case 0: play(SFX_ESTRELLA_1); break;
            case 1: play(SFX_ESTRELLA_2); break;
            case 2: play(SFX_ESTRELLA_3); break;
            default: break;
        }
        nextStarIndex++;
    }

    // ------------------------------------------------------------------ //
    //  PetListener
    // ------------------------------------------------------------------ //
    @Override
    public void onCandyEaten() {
        candyEaten = true;
        play(SFX_COMIENDO);
    }

    @Override public void onCandyClose()   { /* sin sonido */ }
    @Override public void onCandyEscaped() { /* sin sonido */ }

    // ------------------------------------------------------------------ //
    //  ModelListener
    // ------------------------------------------------------------------ //
    /**
     * Se llama en TODOS los fracasos:
     *   - caramelo sale de pantalla (ruta A)
     *   - después de candy.destroy() en pinchos (ruta B)
     * Es el punto único y seguro para disparar no_lo_comio.wav.
     */
    @Override
    public void onFailure() {
        if (!candyEaten && !failSoundPlayed) {
            failSoundPlayed = true;
            play(SFX_NO_LO_COMIO);
        }
    }

    @Override
    public void onLevelCleared() {
        // comiendo.wav ya sonó en onCandyEaten; nada extra aquí.
    }

    // ------------------------------------------------------------------ //
    //  AirCushionListener
    // ------------------------------------------------------------------ //
    @Override
    public void onAirCushionFire() {
        play(SFX_BOLSA_AIRE);
    }

    // ------------------------------------------------------------------ //
    //  CandyListener
    // ------------------------------------------------------------------ //
    /**
     * Solo se dispara cuando Candy.destroy() es llamado explícitamente
     * (p.ej. pinchos). No hacemos nada aquí: onFailure() lo manejará
     * inmediatamente después con la guardia failSoundPlayed.
     */
    @Override
    public void onCandyDestroyed() { /* onFailure() se encarga */ }

    // ------------------------------------------------------------------ //
    //  Utilidad
    // ------------------------------------------------------------------ //
    private void play(String fileName) {
        if (audioManager != null) {
            audioManager.reproducirSFX(fileName);
        }
    }
}
