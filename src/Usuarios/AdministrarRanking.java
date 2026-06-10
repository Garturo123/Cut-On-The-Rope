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

        synchronized (this) {

            int puntuacion = calcularPuntuacionCompuesta(jugador);

            puntuacionesGlobales.put(
                    jugador.getNombreUsuario(),
                    puntuacion
            );

            actualizarListaRanking();
        }
    }

    private int calcularPuntuacionCompuesta(Jugador jugador) {

        Estadisticas stats = jugador.getEstadisticas();

        int puntuacion = 0;

        puntuacion += stats.getNivelesCompletados() * 100;
        puntuacion += (stats.getTiempoTotalJugado() / 60) * 10;
        puntuacion += stats.getPartidasGanadas() * 50;
        puntuacion -= stats.getIntentosFallidos() * 5;

        if (stats.getTiempoPromedioPorNivel() < 30
                && stats.getTiempoPromedioPorNivel() > 0) {
            puntuacion += 200;
        }

        return Math.max(0, puntuacion);
    }

    private void actualizarListaRanking() {
        ultimaActualizacion = new Date();
    }

    @Override
    public List<Jugador> obtenerTopJugadores(int cantidad) {

        return jugadores.stream()
                .sorted((j1, j2) -> Integer.compare(
                puntuacionesGlobales.getOrDefault(
                        j2.getNombreUsuario(), 0),
                puntuacionesGlobales.getOrDefault(
                        j1.getNombreUsuario(), 0)))
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    @Override
    public int obtenerPosicion(Jugador jugador) {

        List<Jugador> ranking =
                obtenerTopJugadores(jugadores.size());

        for (int i = 0; i < ranking.size(); i++) {

            if (ranking.get(i).equals(jugador)) {
                return i + 1;
            }

        }

        return -1;
    }

    public int obtenerPuntuacion(Jugador jugador) {

        return puntuacionesGlobales.getOrDefault(
                jugador.getNombreUsuario(),
                0
        );
    }

    public void agregarJugador(Jugador jugador) {

        if (!jugadores.contains(jugador)) {
            jugadores.add(jugador);
            actualizarRanking(jugador);
        }
    }

    public Date getUltimaActualizacion() {
        return ultimaActualizacion;
    }
}