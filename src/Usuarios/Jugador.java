/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Clase principal que representa un jugador del sistema
 * Implementa Serializable para almacenamiento en archivos binarios
 */
public class Jugador implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ============ ATRIBUTOS PRINCIPALES ============
    
    /** Identificador único del jugador (usado para login) */
    private String nombreUsuario;
    
    /** Contraseña del jugador (almacenada de forma segura) */
    private String contrasena;
    
    /** Nombre completo del jugador */
    private String nombreCompleto;
    
    /** Fecha de registro en el sistema */
    private Date fechaRegistro;
    
    /** Fecha de la última sesión iniciada */
    private Date ultimaSesion;
    
    /** Progreso del juego (nivel actual, puntajes, etc.) */
    private ProgresoJuego progreso;
    
    /** Tiempo total jugado en segundos */
    private long tiempoTotalJugado;
    
    /** Historial de partidas jugadas */
    private List<RegistroPartida> historialPartidas;
    
    /** Preferencias de configuración del juego */
    private PreferenciasJuego preferencias;
    
    /** Ruta del avatar o imagen de perfil */
    private String rutaAvatar;
    
    /** Datos estadísticos del jugador */
    private Estadisticas estadisticas;
    
    /** Lista de amigos o rivales */
    private List<String> listaAmigos;
    
    /** Mapa de puntuaciones por categoría para ranking */
    private Map<String, Integer> puntuacionesRanking;
    
    // ============ CONSTRUCTORES ============
    
    /**
     * Constructor básico para nuevo jugador
     */
    public Jugador(String nombreUsuario, String contrasena, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = new Date();
        this.ultimaSesion = new Date();
        this.tiempoTotalJugado = 0;
        this.historialPartidas = new ArrayList<>();
        this.preferencias = new PreferenciasJuego();
        this.listaAmigos = new ArrayList<>();
        this.puntuacionesRanking = new HashMap<>();
        this.progreso = new ProgresoJuego();
        this.estadisticas = new Estadisticas();
        this.rutaAvatar = "avatars/default.png";
    }
    
    /**
     * Constructor completo con todos los parámetros
     */
    public Jugador(String nombreUsuario, String contrasena, String nombreCompleto,
                   Date fechaRegistro, String rutaAvatar) {
        this(nombreUsuario, contrasena, nombreCompleto);
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : new Date();
        this.rutaAvatar = rutaAvatar != null ? rutaAvatar : "avatars/default.png";
    }
    
    // ============ MÉTODOS DE AUTENTICACIÓN ============
    
    /**
     * Verifica si la contraseña proporcionada es correcta
     */
    public boolean verificarContrasena(String contrasenaIngresada) {
        return this.contrasena.equals(contrasenaIngresada);
    }
    
    /**
     * Cambia la contraseña después de validar la actual
     */
    public boolean cambiarContrasena(String contrasenaActual, String nuevaContrasena) {
        if (verificarContrasena(contrasenaActual)) {
            // Validar requisitos de contraseña
            if (validarRequisitosContrasena(nuevaContrasena)) {
                this.contrasena = nuevaContrasena;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Valida que la contraseña cumpla con los requisitos mínimos
     * - Mínimo 6 caracteres
     * - Al menos 1 número
     * - Al menos 1 mayúscula
     */
    private boolean validarRequisitosContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 6) {
            return false;
        }
        boolean tieneNumero = contrasena.matches(".*\\d.*");
        boolean tieneMayuscula = !contrasena.equals(contrasena.toLowerCase());
        return tieneNumero && tieneMayuscula;
    }
    
    // ============ MÉTODOS DE SESIÓN ============
    
    /**
     * Registra el inicio de sesión actualizando la última sesión
     */
    public void registrarInicioSesion() {
        this.ultimaSesion = new Date();
    }
    
    /**
     * Calcula los días desde el último inicio de sesión
     */
    public long diasDesdeUltimaSesion() {
        long diff = new Date().getTime() - ultimaSesion.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }
    
    /**
     * Actualiza el tiempo total jugado
     */
    public void agregarTiempoJugado(long segundos) {
        this.tiempoTotalJugado += segundos;
        if (this.estadisticas != null) {
            this.estadisticas.setTiempoTotalJugado(this.tiempoTotalJugado);
        }
    }
    
    // ============ MÉTODOS DE PROGRESO ============
    
    /**
     * Registra la finalización de un nivel
     */
    public void completarNivel(int nivel, int puntuacion, int tiempoSegundos) {
        if (progreso != null) {
            progreso.completarNivel(nivel, puntuacion, tiempoSegundos);
        }
        
        // Crear registro de partida
        RegistroPartida partida = new RegistroPartida();
        partida.setNivel(nivel);
        partida.setPuntuacion(puntuacion);
        partida.setTiempoSegundos(tiempoSegundos);
        partida.setCompletada(true);
        partida.setFecha(new Date());
        
        historialPartidas.add(partida);
        
        // Actualizar estadísticas
        if (estadisticas != null) {
            estadisticas.registrarPartida(partida);
            estadisticas.actualizarRankingPuntuacion(puntuacion);
        }
        
        // Actualizar puntuación para ranking global
        actualizarPuntuacionRanking("global", calcularPuntuacionGlobal());
    }
    
    /**
     * Registra un intento fallido de nivel
     */
    public void registrarIntentoFallido(int nivel) {
        RegistroPartida partida = new RegistroPartida();
        partida.setNivel(nivel);
        partida.setCompletada(false);
        partida.setFecha(new Date());
        
        historialPartidas.add(partida);
        
        if (estadisticas != null) {
            estadisticas.registrarIntentoFallido();
        }
    }
    
    /**
     * Calcula la puntuación global del jugador para ranking
     */
    private int calcularPuntuacionGlobal() {
    int puntuacion = 0;

    if (estadisticas != null) {
        puntuacion += estadisticas.getNivelesCompletados() * 100;
        puntuacion += estadisticas.getPartidasGanadas() * 50;
        puntuacion += Math.max(0, 1000 - (int)estadisticas.getTiempoPromedioPorNivel());
    }

    return puntuacion;
}
    /**
     * Actualiza la puntuación en una categoría específica del ranking
     */
    private void actualizarPuntuacionRanking(String categoria, int puntuacion) {
        puntuacionesRanking.put(categoria, puntuacion);
    }
    
    // ============ MÉTODOS DE AMIGOS ============
    
    /**
     * Agrega un amigo a la lista
     */
    public boolean agregarAmigo(String nombreAmigo) {
        if (!listaAmigos.contains(nombreAmigo) && !nombreUsuario.equals(nombreAmigo)) {
            listaAmigos.add(nombreAmigo);
            return true;
        }
        return false;
    }
    
    /**
     * Elimina un amigo de la lista
     */
    public boolean eliminarAmigo(String nombreAmigo) {
        return listaAmigos.remove(nombreAmigo);
    }
    
    /**
     * Obtiene la lista de amigos
     */
    public List<String> getListaAmigos() {
        return new ArrayList<>(listaAmigos);
    }
    
    // ============ GETTERS Y SETTERS ============
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public Date getFechaRegistro() {
        return fechaRegistro != null ? new Date(fechaRegistro.getTime()) : null;
    }
    
    public Date getUltimaSesion() {
        return ultimaSesion != null ? new Date(ultimaSesion.getTime()) : null;
    }
    
    public ProgresoJuego getProgreso() {
        return progreso;
    }
    
    public void setProgreso(ProgresoJuego progreso) {
        this.progreso = progreso;
    }
    
    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }
    
    public void setTiempoTotalJugado(long tiempoTotalJugado) {
        this.tiempoTotalJugado = tiempoTotalJugado;
    }
    
    public List<RegistroPartida> getHistorialPartidas() {
        return new ArrayList<>(historialPartidas);
    }
    
    public PreferenciasJuego getPreferencias() {
        return preferencias;
    }
    
    public void setPreferencias(PreferenciasJuego preferencias) {
        this.preferencias = preferencias;
    }
    
    public String getRutaAvatar() {
        return rutaAvatar;
    }
    
    public void setRutaAvatar(String rutaAvatar) {
        this.rutaAvatar = rutaAvatar;
    }
    
    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
    
    public void setEstadisticas(Estadisticas estadisticas) {
        this.estadisticas = estadisticas;
    }
    
    public Map<String, Integer> getPuntuacionesRanking() {
        return new HashMap<>(puntuacionesRanking);
    }
    
    public int getPuntuacionRanking(String categoria) {
        return puntuacionesRanking.getOrDefault(categoria, 0);
    }
    
    // ============ MÉTODOS UTILITARIOS ============
    
    /**
     * Obtiene un resumen formateado del jugador
     */
    public String obtenerResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DEL JUGADOR ===\n");
        sb.append("Usuario: ").append(nombreUsuario).append("\n");
        sb.append("Nombre: ").append(nombreCompleto).append("\n");
        sb.append("Registro: ").append(fechaRegistro).append("\n");
        sb.append("Última sesión: ").append(ultimaSesion).append("\n");
        sb.append("Tiempo jugado: ").append(tiempoTotalJugado / 60).append(" minutos\n");
        sb.append("Nivel actual: ").append(progreso != null ? progreso.getNivelActual() : 1).append("\n");
        sb.append("Amigos: ").append(listaAmigos.size()).append("\n");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jugador jugador = (Jugador) obj;
        return Objects.equals(nombreUsuario, jugador.nombreUsuario);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario);
    }
    
    @Override
    public String toString() {
        return String.format("Jugador{usuario='%s', nombre='%s', nivel=%d, puntuación=%d}",
                nombreUsuario, nombreCompleto,
                progreso != null ? progreso.getNivelActual() : 1,
                getPuntuacionRanking("global"));
    }
}