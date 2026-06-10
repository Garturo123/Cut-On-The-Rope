
package Usuarios;

import java.io.Serializable;
import java.util.Date;

public class RegistroPartida implements Serializable {
    private Date fecha;
    private int nivel;
    private int tiempoSegundos;
    private int puntuacion;
    private boolean completada;
    private int intentos;
    private int estrellasRecolectadas; // Para Cut the Rope
    private int movimientosRealizados;   // Para Sokoban
    
    // Constructor, getters y setters...

    public Date getFecha() {
        return fecha;
    }

    public int getNivel() {
        return nivel;
    }

    public int getTiempoSegundos() {
        return tiempoSegundos;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public int getIntentos() {
        return intentos;
    }

    public int getEstrellasRecolectadas() {
        return estrellasRecolectadas;
    }

    public int getMovimientosRealizados() {
        return movimientosRealizados;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setTiempoSegundos(int tiempoSegundos) {
        this.tiempoSegundos = tiempoSegundos;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public void setEstrellasRecolectadas(int estrellasRecolectadas) {
        this.estrellasRecolectadas = estrellasRecolectadas;
    }

    public void setMovimientosRealizados(int movimientosRealizados) {
        this.movimientosRealizados = movimientosRealizados;
    }
    
}
