package Audio;


import java.io.Serializable;

public class Config implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int volumenSFX = 80;
    private int volumenMusica = 60;
    private boolean sfxActivo = true;
    private boolean musicaActiva = true;
    private double posicionMusicaSegundos = 0;
    protected boolean isMute = (sfxActivo && musicaActiva);
    
    public Config(){}
    public Config(int volSFX, int volMus, boolean sfxAct, boolean musAct, double pos) {
        this.volumenSFX = volSFX;
        this.volumenMusica = volMus;
        this.sfxActivo = sfxAct;
        this.musicaActiva = musAct;
        this.posicionMusicaSegundos = pos;
    }
    
    // Getters y Setters
    public int getVolumenSFX() { return volumenSFX; }
    public void setVolumenSFX(int volumen) { this.volumenSFX = Math.max(0, Math.min(100, volumen)); this.sfxActivo = volumen > 0; }
    
    public int getVolumenMusica() { return volumenMusica; }
    public void setVolumenMusica(int volumen) { this.volumenMusica = Math.max(0, Math.min(100, volumen)); this.musicaActiva = volumen > 0; }
    
    public boolean isSfxActivo() { return sfxActivo; }
    public boolean isMusicaActiva() { return musicaActiva; }
    public double getPosicionMusica() { return posicionMusicaSegundos; }
    public void setPosicionMusica(double pos) { this.posicionMusicaSegundos = Math.max(0, pos); }
    
    public void toggleMute() {
        boolean nuevoEstado = !(sfxActivo || musicaActiva);
        sfxActivo = nuevoEstado;
        musicaActiva = nuevoEstado;
    }
}