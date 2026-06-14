package Usuarios;

import java.io.Serializable;

public class Niveles implements Serializable {
    private static final long serialVersionUID = 1L;

    private int nivel;
    private String dificultad;
    private boolean completado;
    private int mejorPuntaje;
    private int mejoresEstrellas;
    private long mejorTiempo;
    private String fechaCompletado;

    public Niveles(int nivel, String dificultad) {
        this.nivel = nivel;
        this.dificultad = dificultad;
        this.completado = false;
        this.mejorPuntaje = 0;
        this.mejoresEstrellas = 0;
        this.mejorTiempo = 0;
        this.fechaCompletado = null;
    }

    public int getNivel() { return nivel; }
    public String getDificultad() { return dificultad; }
    public boolean isCompletado() { return completado; }
    public int getMejorPuntaje() { return mejorPuntaje; }
    public int getMejoresEstrellas() { return mejoresEstrellas; }
    public long getMejorTiempo() { return mejorTiempo; }
    public String getFechaCompletado() { return fechaCompletado; }

    public void setNivel(int nivel) { this.nivel = nivel; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }
    public void setCompletado(boolean completado) { this.completado = completado; }
    public void setMejorPuntaje(int puntaje) { this.mejorPuntaje = puntaje; }
    public void setMejoresEstrellas(int estrellas) { this.mejoresEstrellas = normalizarEstrellas(estrellas); }
    public void setMejorTiempo(long tiempo) { this.mejorTiempo = tiempo; }
    public void setFechaCompletado(String fecha) { this.fechaCompletado = fecha; }

    public void completarNivel(int puntaje, long tiempoSegundos) {
        completarNivel(puntaje, tiempoSegundos, 0);
    }

    public void completarNivel(int puntaje, long tiempoSegundos, int estrellas) {
        this.completado = true;
        this.mejorPuntaje = Math.max(this.mejorPuntaje, puntaje);
        this.mejoresEstrellas = Math.max(this.mejoresEstrellas, normalizarEstrellas(estrellas));

        if (this.mejorTiempo == 0 || tiempoSegundos < this.mejorTiempo) {
            this.mejorTiempo = tiempoSegundos;
        }

        this.fechaCompletado = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }

    public String getTiempoFormateado() {
        long minutos = mejorTiempo / 60;
        long segundos = mejorTiempo % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    public int calcularPuntajeBase() {
        if (nivel >= 1 && nivel <= 2) return 50;
        if (nivel >= 3 && nivel <= 4) return 100;
        if (nivel >= 5 && nivel <= 6) return 150;
        if (nivel >= 7 && nivel <= 8) return 200;
        if (nivel >= 9 && nivel <= 10) return 250;
        return 0;
    }

    private int normalizarEstrellas(int estrellas) {
        return Math.max(0, Math.min(3, estrellas));
    }

    @Override
    public String toString() {
        return String.format("Nivel %d - %s [%s] - Estrellas: %d - Puntaje: %d - Tiempo: %s",
            nivel, dificultad, completado ? "Completado" : "Bloqueado", mejoresEstrellas, mejorPuntaje, getTiempoFormateado());
    }
}