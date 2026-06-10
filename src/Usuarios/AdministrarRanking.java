/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author gaat1
 */
public class AdministrarRanking extends Rankings implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> puntuacionesGlobales;
    private Date ultimaActualizacion;
    
    public AdministrarRanking() {
        this.jugadores = new ArrayList<>();
        this.puntuacionesGlobales = new HashMap<>();
        this.ultimaActualizacion = new Date();
    }
    
    @Override
    public void actualizarRanking(Jugador jugador) {
        synchronized(this) {
            // Calcular puntuación basada en múltiples factores
            int puntuacion = calcularPuntuacionCompuesta(jugador);
            puntuacionesGlobales.put(jugador.getNombreUsuario(), puntuacion);
            
            // Ordenar y actualizar posiciones
            actualizarListaRanking();
        }
    }
    
    private int calcularPuntuacionCompuesta(Jugador jugador) {
        Estadisticas stats = jugador.getEstadisticas();
        int puntuacion = 0;
        
        // Fórmula de puntuación personalizable
        puntuacion += stats.getNivelesCompletados() * 100;
        puntuacion += (stats.getTiempoTotalJugado() / 60) * 10; // 10 pts por minuto
        puntuacion += stats.getPartidasGanadas() * 50;
        puntuacion -= stats.getIntentosFallidos() * 5;
        
        // Bonus por velocidad (menor tiempo promedio)
        if (stats.getTiempoPromedioPorNivel() < 30) {
            puntuacion += 200;
        }
        
        return Math.max(0, puntuacion);
    }
    
    @Override
    public List<Jugador> obtenerTopJugadores(int cantidad) {
        return jugadores.stream()
            .sorted((j1, j2) -> Integer.compare(
                puntuacionesGlobales.get(j2.getNombreUsuario()),
                puntuacionesGlobales.get(j1.getNombreUsuario())
            ))
            .limit(cantidad)
            .collect(Collectors.toList());
    }

    @Override
    public int obtenerPosicion(Object jugador) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}