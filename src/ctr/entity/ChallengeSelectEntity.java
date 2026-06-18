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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<String, BufferedImage> avatarCache = new HashMap<String, BufferedImage>();
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
        btnVolver.setListener(() -> scene.cambiarAState(GameState.SOCIAL_MENU));
    }

    private void enviarReto() {
        List<Usuario> jugadores = getJugadores();
        if (selectedIndex < 0 || selectedIndex >= jugadores.size()) {
            mensaje = I18n.t("select_player");
        } else if (isCurrentUser(jugadores.get(selectedIndex))) {
            mensaje = I18n.t("cant_challenge_self");
        } else {
            String rival = jugadores.get(selectedIndex).getUsername();
            mensaje = menus.iniciarChallenge(rival);
            if ("Challenge iniciado".equals(mensaje)) {
                scene.startFriendlyChallenge(rival);
            }
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
        g.drawString(I18n.t("ranking_title"), 345, 80);
        g.setFont(g.getFont().deriveFont(15f));
        g.drawString(I18n.t("ranking_rules_1"), 145, 112);
        g.drawString(I18n.t("ranking_rules_2"), 145, 132);

        if (!scene.getLastChallengeResult().isEmpty()) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(15f));
            g.drawString(scene.getLastChallengeResult(), 90, 575);
        }

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
            g.drawString(mensaje, 90, 595);
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

            BufferedImage avatar = getAvatar(jugador.getAvatar());
            g.drawImage(avatar, ROW_X + 10, y + 8, 46, 46, null);
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString("#" + (i + 1), ROW_X + 70, y + 30);
            g.drawString(shortName(jugador.getUsername()), ROW_X + 130, y + 30);
            g.setFont(g.getFont().deriveFont(15f));
            String relation = isFriend(jugador) ? I18n.t("friend") : I18n.t("player");
            if (isCurrentUser(jugador)) relation = I18n.t("you");
            g.drawString(relation, ROW_X + 270, y + 27);
            g.drawString(I18n.t("levels_completed") + menus.nivelesCompletados(jugador.getUsername()), ROW_X + 350, y + 27);
            g.drawString(I18n.t("stars") + ": " + menus.totalEstrellas(jugador.getUsername()), ROW_X + 620, y + 27);
        }
    }

    private List<Usuario> getJugadores() {
        ArrayList<Usuario> todos = menus.cargarTodosUsuarios();
        ArrayList<Usuario> jugadores = new ArrayList<Usuario>();
        for (Usuario usuario : todos) {
            if (usuario.isCuentaActiva()) {
                jugadores.add(usuario);
            }
        }
        Collections.sort(jugadores, new Comparator<Usuario>() {
            @Override
            public int compare(Usuario a, Usuario b) {
                int byLevels = menus.nivelesCompletados(b.getUsername()) - menus.nivelesCompletados(a.getUsername());
                if (byLevels != 0) return byLevels;
                int byStars = menus.totalEstrellas(b.getUsername()) - menus.totalEstrellas(a.getUsername());
                if (byStars != 0) return byStars;
                return a.getUsername().compareToIgnoreCase(b.getUsername());
            }
        });
        return jugadores;
    }

    private boolean isCurrentUser(Usuario usuario) {
        Usuario actual = sessionManager.getUsuarioActual();
        return actual != null && usuario != null && actual.getUsername().equals(usuario.getUsername());
    }

    private boolean isFriend(Usuario usuario) {
        Usuario actual = sessionManager.getUsuarioActual();
        return actual != null && usuario != null && actual.getAmigosRivales().contains(usuario.getUsername());
    }

    private BufferedImage getAvatar(String avatarName) {
        BufferedImage avatar = avatarCache.get(avatarName);
        if (avatar == null) {
            avatar = loadImageFromResource("/res/" + avatarName);
            avatarCache.put(avatarName, avatar);
        }
        return avatar;
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
