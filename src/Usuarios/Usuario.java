package Usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Datos básicos
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private Date fechaRegistro;
    private Date ultimaSesion;
    private boolean cuentaActiva;
    private boolean estadoCuentaInicializado;
    
    // Estadísticas
    private int nivelesCompletados;
    private int retosGanados;
    private int puntuacionGeneral;
    private int nivelDesbloqueado;
    private ArrayList<String> amigosRivales;
    private ArrayList<String> historialRetos;
    private ArrayList<String> partidasRegistradas;
    
    // Configuración de perfil
    private String avatar;
    private String avatarColorHex;
    private int volumenSFX;
    private int volumenMusica;
    private boolean sfxActivo;
    private boolean musicaActiva;
    private double posicionMusicaSegundos;
    private String idioma;
    private String controles;
    
    public Usuario(String username, String passwordHash, String nombreCompleto) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = new Date();
        this.ultimaSesion = new Date();
        this.cuentaActiva = true;
        this.estadoCuentaInicializado = false;
        
        this.nivelesCompletados = 0;
        this.retosGanados = 0;
        this.puntuacionGeneral = 0;
        this.nivelDesbloqueado = 1;
        this.amigosRivales = new ArrayList<>();
        this.historialRetos = new ArrayList<>();
        this.partidasRegistradas = new ArrayList<>();
        
        this.avatar = "avatar_1.png";
        this.avatarColorHex = "#a2b794";
        this.volumenSFX = 80;
        this.volumenMusica =60;
        this.sfxActivo = true;
        this.musicaActiva = true;
        this.posicionMusicaSegundos = 0;
        this.idioma = "Spanish";
        this.controles = "default";
    }
    
    // Getters y Setters básicos
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public Date getFechaRegistro() { return fechaRegistro; }
    public Date getUltimaSesion() { return ultimaSesion; }
    
    public boolean isCuentaActiva() { return cuentaActiva; }
    public void setCuentaActiva(boolean cuentaActiva) { this.cuentaActiva = cuentaActiva; }
    
    public boolean isEstadoCuentaInicializado() { return estadoCuentaInicializado; }
    public void setEstadoCuentaInicializado(boolean estado) { this.estadoCuentaInicializado = estado; }
    
    // Estadísticas
    public int getNivelesCompletados() { return nivelesCompletados; }
    public void setNivelesCompletados(int nivelesCompletados) { this.nivelesCompletados = nivelesCompletados; }
    
    public int getRetosGanados() { return retosGanados; }
    public void setRetosGanados(int retosGanados) { this.retosGanados = retosGanados; }
    
    public int getPuntuacionGeneral() { return puntuacionGeneral; }
    public void setPuntuacionGeneral(int puntuacionGeneral) { this.puntuacionGeneral = puntuacionGeneral; }
    
    public int getNivelDesbloqueado() { return nivelDesbloqueado; }
    public void setNivelDesbloqueado(int nivelDesbloqueado) { this.nivelDesbloqueado = nivelDesbloqueado; }
    
    // Amigos
    public ArrayList<String> getAmigosRivales() { return amigosRivales; }
    
    public void agregarAmigoRival(String amigo) {
        if (!amigosRivales.contains(amigo)) {
            amigosRivales.add(amigo);
        }
    }
    
    public void eliminarAmigoRival(String amigo) {
        amigosRivales.remove(amigo);
    }
    
    // Retos
    public ArrayList<String> getHistorialRetos() { return historialRetos; }
    public void agregarReto(String reto) { historialRetos.add(reto); }
    
    public ArrayList<String> getPartidasRegistradas() { return partidasRegistradas; }
    
    // Avatar
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public String getAvatarColorHex() { return avatarColorHex; }
    public void setAvatarColorHex(String avatarColorHex) { this.avatarColorHex = avatarColorHex; }
    
    // Configuración de audio
    public int getVolumenSFX() { return volumenSFX; }
    public void setVolumenSFX(int volumen) { this.volumenSFX = Math.max(0, Math.min(100, volumen)); }
    
    public int getVolumenMusica() { return volumenMusica; }
    public void setVolumenMusica(int volumen) { this.volumenMusica = Math.max(0, Math.min(100, volumen)); }
    
    public boolean isSfxActivo() { return sfxActivo; }
    public void setSfxActivo(boolean activo) { this.sfxActivo = activo; }
    
    public boolean isMusicaActiva() { return musicaActiva; }
    public void setMusicaActiva(boolean activa) { this.musicaActiva = activa; }
    
    public double getPosicionMusicaSegundos() { return posicionMusicaSegundos; }
    public void setPosicionMusicaSegundos(double posicion) { this.posicionMusicaSegundos = posicion; }
    
    // Configuración general
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    
    public String getControles() { return controles; }
    public void setControles(String controles) { this.controles = controles; }
    
    // Métodos de sesión
    public void iniciarSesion() {
        this.ultimaSesion = new Date();
    }
    
    public void cerrarSesion() {
        // Solo actualiza la última sesión
        this.ultimaSesion = new Date();
    }
    
    // Métodos de estadísticas
    public void registrarPartida(boolean gano, int nivel, int puntos, long tiempoJugado, String detalle) {
        this.puntuacionGeneral += puntos;
        
        if (gano) {
            if (nivel == this.nivelDesbloqueado && nivel < 10) {
                this.nivelDesbloqueado++;
            }
            this.retosGanados++;
        }
        
        String registro = String.format("%s|%b|%d|%d|%d|%s", 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
            gano, nivel, puntos, tiempoJugado, detalle);
        partidasRegistradas.add(registro);
    }
    
    // Actualización de perfil
    public void actualizarPerfil(String avatar, int volumen, String idioma, String controles) {
        if (avatar != null) this.avatar = avatar;
        if (volumen >= 0 && volumen <= 100) {
            this.volumenSFX = volumen;
            this.volumenMusica = volumen;
        }
        if (idioma != null) this.idioma = idioma;
        if (controles != null) this.controles = controles;
    }
    
    public void actualizarConfigAudio(int volSFX, int volMus, boolean sfxAct, boolean musAct, double pos) {
        this.volumenSFX = volSFX;
        this.volumenMusica = volMus;
        this.sfxActivo = sfxAct;
        this.musicaActiva = musAct;
        this.posicionMusicaSegundos = pos;
    }
}