/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProgresoJuego implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int nivelActual;
    private int nivelesDesbloqueados;
    private Map<Integer, Boolean> nivelesCompletados;
    private Map<Integer, Integer> mejoresPuntuaciones;
    private Map<Integer, Integer> mejoresTiempos;
    
    public ProgresoJuego() {
        this.nivelActual = 1;
        this.nivelesDesbloqueados = 1;
        this.nivelesCompletados = new HashMap<>();
        this.mejoresPuntuaciones = new HashMap<>();
        this.mejoresTiempos = new HashMap<>();
    }
    
    public void completarNivel(int nivel, int puntuacion, int tiempo) {
        nivelesCompletados.put(nivel, true);
        nivelesDesbloqueados = Math.max(nivelesDesbloqueados, nivel + 1);
        nivelActual = nivel + 1;
        
        if (puntuacion > mejoresPuntuaciones.getOrDefault(nivel, 0)) {
            mejoresPuntuaciones.put(nivel, puntuacion);
        }
        
        if (tiempo < mejoresTiempos.getOrDefault(nivel, Integer.MAX_VALUE)) {
            mejoresTiempos.put(nivel, tiempo);
        }
    }
    
    public boolean isNivelCompletado(int nivel) {
        return nivelesCompletados.getOrDefault(nivel, false);
    }
    
    public boolean isNivelDesbloqueado(int nivel) {
        return nivel <= nivelesDesbloqueados;
    }
    
    // Getters y setters
    public int getNivelActual() { return nivelActual; }
    public void setNivelActual(int nivelActual) { this.nivelActual = nivelActual; }
    public int getNivelesDesbloqueados() { return nivelesDesbloqueados; }
    public Map<Integer, Integer> getMejoresPuntuaciones() { return mejoresPuntuaciones; }
    public Map<Integer, Integer> getMejoresTiempos() { return mejoresTiempos; }
}