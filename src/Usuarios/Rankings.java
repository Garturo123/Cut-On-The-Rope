package Usuarios;

import java.util.List;

/**
 *
 * @author gaat1
 */
public abstract class Rankings {
    protected List<Jugador> jugadores;
    
    public abstract void actualizarRanking(Jugador jugador);
    public abstract List<Jugador> obtenerTopJugadores(int cantidad);
    public abstract int obtenerPosicion(Jugador jugador);
}
