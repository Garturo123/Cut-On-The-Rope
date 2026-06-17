package ctr.entity;

import Usuarios.Menu;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import ctr.Entity;
import ctr.I18n;
import ctr.Mouse;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ChallengeSelectEntity extends Entity {
    private static final int ROW_X = 90;
    private static final int ROW_Y = 160;
    private static final int ROW_W = 810;
    private static final int ROW_H = 70;
    private static final int MAX_ROWS = 6;

    private final Menu menus;
    private final SessionManager sessionManager;
    private final Button btnReto;
    private final Button btnVolver;
    private int selectedIndex = -1;
    private String mensaje = "";
    private int contadorMensaje;

    public ChallengeSelectEntity(Scene scene, Menu menus, SessionManager sessionManager) {
        super(scene);
        this.menus = menus;
        this.sessionManager = sessionManager;
        btnReto = new Button(scene, I18n.t("challenge"), 28, 42, 275, 610);
        btnVolver = new Button(scene, I18n.t("back"), 50, 42, 555, 610);
        btnReto.setListener(() -> enviarReto());
        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
    }

    private void enviarReto() {
        List<Usuario> jugadores = getJugadores();
        if (selectedIndex < 0 || selectedIndex >= jugadores.size()) {
            mensaje = I18n.t("select_player");
        } else {
            mensaje = menus.iniciarChallenge(jugadores.get(selectedIndex).getUsername());
        }
        contadorMensaje = 150;
    }

    @Override
    public void update() {
        if (!visible) return;
        updateSelection();
        btnReto.update();
        btnVolver.update();
        if (contadorMensaje > 0) contadorMensaje--;
    }

    private void updateSelection() {
        if (!Mouse.pressed || Mouse.pressedConsumed) return;
        List<Usuario> jugadores = getJugadores();
        int rows = Math.min(MAX_ROWS, jugadores.size());
        for (int i = 0; i < rows; i++) {
            int y = ROW_Y + i * ROW_H;
            if (Mouse.x >= ROW_X && Mouse.x <= ROW_X + ROW_W && Mouse.y >= y && Mouse.y <= y + ROW_H - 8) {
                selectedIndex = i;
                Mouse.pressedConsumed = true;
                return;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setColor(new Color(0, 0, 0, 215));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString(I18n.t("players_title"), 425, 95);

        List<Usuario> jugadores = getJugadores();
        if (jugadores.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString(I18n.t("no_players"), 390, 300);
        } else {
            drawRows(g, jugadores);
        }

        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(15f));
            g.drawString(mensaje, 90, 585);
        }

        btnReto.draw(g);
        btnVolver.draw(g);
    }

    private void drawRows(Graphics2D g, List<Usuario> jugadores) {
        int rows = Math.min(MAX_ROWS, jugadores.size());
        for (int i = 0; i < rows; i++) {
            Usuario jugador = jugadores.get(i);
            int y = ROW_Y + i * ROW_H;
            g.setColor(i == selectedIndex ? new Color(255, 210, 90, 90) : new Color(255, 255, 255, 35));
            g.fillRoundRect(ROW_X, y, ROW_W, ROW_H - 8, 10, 10);

            BufferedImage avatar = loadImageFromResource("/res/" + jugador.getAvatar());
            g.drawImage(avatar, ROW_X + 10, y + 8, 46, 46, null);
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString(shortName(jugador.getUsername()), ROW_X + 75, y + 30);
            g.setFont(g.getFont().deriveFont(15f));
            g.drawString(I18n.t("score") + ": " + jugador.getPuntuacionGeneral(), ROW_X + 300, y + 27);
            g.drawString(I18n.t("stars") + ": " + menus.totalEstrellas(jugador.getUsername()), ROW_X + 520, y + 27);
        }
    }

    private List<Usuario> getJugadores() {
        Usuario actual = sessionManager.getUsuarioActual();
        ArrayList<Usuario> todos = menus.cargarTodosUsuarios();
        ArrayList<Usuario> jugadores = new ArrayList<Usuario>();
        for (Usuario usuario : todos) {
            if (actual == null || !usuario.getUsername().equals(actual.getUsername())) {
                jugadores.add(usuario);
            }
        }
        return jugadores;
    }

    private String shortName(String username) {
        if (username == null) return "";
        return username.length() <= 16 ? username : username.substring(0, 13) + "...";
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = newGameState == GameState.CHALLENGE_SELECT;
        if (visible) {
            refreshText();
            selectedIndex = -1;
            mensaje = "";
            contadorMensaje = 0;
            btnReto.reset();
            btnVolver.reset();
        }
    }

    private void refreshText() {
        btnReto.setText(I18n.t("challenge"));
        btnVolver.setText(I18n.t("back"));
    }
}
