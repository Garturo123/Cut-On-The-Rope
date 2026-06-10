package Usuarios;

import java.awt.Color;
import java.awt.Label;
import java.awt.ScrollPane;
import java.util.List;
import jdk.javadoc.internal.doclets.formats.html.Table;

public class PantallaRanking implements Screen {
    private Stage stage;
    private Table tablaRanking;
    private ScrollPane scrollPane;
    private AdministrarRanking rankingManager;
    private Jugador jugadorActual;
    private Thread actualizadorRanking;
    private volatile boolean actualizando = true;
    
    public PantallaRanking(Jugador jugador) {
        this.jugadorActual = jugador;
        this.rankingManager = new AdministrarRanking();
        this.stage = new Stage();
        
        inicializarUI();
        iniciarActualizacionAutomatica();
    }
    
    private void inicializarUI() {
        tablaRanking = new Table();
        tablaRanking.setFillParent(true);
        
        // Encabezados
        tablaRanking.add("Posición").width(100);
        tablaRanking.add("Usuario").width(200);
        tablaRanking.add("Puntuación").width(150);
        tablaRanking.add("Niveles").width(100);
        tablaRanking.add("Tiempo Total").width(150);
        tablaRanking.row();
        
        // Cargar datos iniciales
        cargarRanking();
        
        scrollPane = new ScrollPane(tablaRanking);
        stage.addActor(scrollPane);
    }
    
    private void cargarRanking() {
        // Usar hilo para no bloquear la UI
        new Thread(() -> {
            List<Jugador> topJugadores = rankingManager.obtenerTopJugadores(50);
            
            Gdx.app.postRunnable(() -> {
                actualizarTabla(topJugadores);
            });
        }).start();
    }
    
    private void actualizarTabla(List<Jugador> jugadores) {
        tablaRanking.clearChildren();
        
        // Re-crear encabezados
        tablaRanking.add("Posición").width(100);
        tablaRanking.add("Usuario").width(200);
        tablaRanking.add("Puntuación").width(150);
        tablaRanking.add("Niveles").width(100);
        tablaRanking.add("Tiempo Total").width(150);
        tablaRanking.row();
        
        int posicion = 1;
        for (Jugador jugador : jugadores) {
            Label.LabelStyle estilo = new Label.LabelStyle();
            if (jugador.equals(jugadorActual)) {
                estilo.fontColor = Color.YELLOW; // Resaltar al jugador actual
            }
            
            tablaRanking.add(String.valueOf(posicion++)).width(100);
            tablaRanking.add(jugador.getNombreUsuario()).width(200);
            tablaRanking.add(String.valueOf(rankingManager.obtenerPuntuacion(jugador))).width(150);
            tablaRanking.add(String.valueOf(jugador.getEstadisticas().getNivelesCompletados())).width(100);
            tablaRanking.add(formatearTiempo(jugador.getEstadisticas().getTiempoTotalJugado())).width(150);
            tablaRanking.row();
        }
    }
    
    private void iniciarActualizacionAutomatica() {
        actualizadorRanking = new Thread(() -> {
            while (actualizando) {
                try {
                    Thread.sleep(30000); // Actualizar cada 30 segundos
                    cargarRanking();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        actualizadorRanking.start();
    }
    
    @Override
    public void dispose() {
        actualizando = false;
        if (actualizadorRanking != null) {
            actualizadorRanking.interrupt();
        }
        stage.dispose();
    }
}
