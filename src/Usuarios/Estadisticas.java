package Usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Estadisticas implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Estadísticas básicas
    private int partidasJugadas;
    private int partidasGanadas;
    private int nivelesCompletados;
    private long tiempoTotalJugado; // en segundos
    private double tiempoPromedioPorNivel;
    private int intentosFallidos;
    private int puntuacionMaxima;
    
    // Historial de partidas
    private List<RegistroPartida> historialPartidas;
    
    // Rankings por nivel/juego
    private Map<Integer, Integer> mejoresTiemposPorNivel;
    private Map<Integer, Integer> puntuacionesPorNivel;
    
    public Estadisticas() {
        this.historialPartidas = new ArrayList<>();
        this.mejoresTiemposPorNivel = new HashMap<>();
        this.puntuacionesPorNivel = new HashMap<>();
        this.partidasJugadas = 0;
        this.nivelesCompletados = 0;
    }
    
    public void registrarPartida(RegistroPartida partida) {
        synchronized(this) {
            historialPartidas.add(partida);
            partidasJugadas++;
            
            if (partida.isCompletada()) {
                partidasGanadas++;
                nivelesCompletados++;
                actualizarMejoresTiempos(partida);
            }
            
            if (partida.getIntentos() > 0) {
                intentosFallidos += (partida.getIntentos() - 1);
            }
            
            actualizarTiempoPromedio();
        }
    }
    
    private void actualizarMejoresTiempos(RegistroPartida partida) {
        int nivel = partida.getNivel();
        int tiempo = partida.getTiempoSegundos();
        
        if (!mejoresTiemposPorNivel.containsKey(nivel) || 
            tiempo < mejoresTiemposPorNivel.get(nivel)) {
            mejoresTiemposPorNivel.put(nivel, tiempo);
        }
    }
    
    private void actualizarTiempoPromedio() {
        if (nivelesCompletados > 0) {
            long tiempoTotal = historialPartidas.stream()
                .filter(RegistroPartida::isCompletada)
                .mapToLong(RegistroPartida::getTiempoSegundos)
                .sum();
            tiempoPromedioPorNivel = (double) tiempoTotal / nivelesCompletados;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public int getPartidasGanadas() {
        return partidasGanadas;
    }

    public int getNivelesCompletados() {
        return nivelesCompletados;
    }

    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public double getTiempoPromedioPorNivel() {
        return tiempoPromedioPorNivel;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public int getPuntuacionMaxima() {
        return puntuacionMaxima;
    }

    public List<RegistroPartida> getHistorialPartidas() {
        return historialPartidas;
    }

    public Map<Integer, Integer> getMejoresTiemposPorNivel() {
        return mejoresTiemposPorNivel;
    }

    public Map<Integer, Integer> getPuntuacionesPorNivel() {
        return puntuacionesPorNivel;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public void setPartidasGanadas(int partidasGanadas) {
        this.partidasGanadas = partidasGanadas;
    }

    public void setNivelesCompletados(int nivelesCompletados) {
        this.nivelesCompletados = nivelesCompletados;
    }

    public void setTiempoTotalJugado(long tiempoTotalJugado) {
        this.tiempoTotalJugado = tiempoTotalJugado;
    }

    public void setTiempoPromedioPorNivel(double tiempoPromedioPorNivel) {
        this.tiempoPromedioPorNivel = tiempoPromedioPorNivel;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public void setPuntuacionMaxima(int puntuacionMaxima) {
        this.puntuacionMaxima = puntuacionMaxima;
    }

    public void setHistorialPartidas(List<RegistroPartida> historialPartidas) {
        this.historialPartidas = historialPartidas;
    }

    public void setMejoresTiemposPorNivel(Map<Integer, Integer> mejoresTiemposPorNivel) {
        this.mejoresTiemposPorNivel = mejoresTiemposPorNivel;
    }

    public void setPuntuacionesPorNivel(Map<Integer, Integer> puntuacionesPorNivel) {
        this.puntuacionesPorNivel = puntuacionesPorNivel;
    }
    
   
}