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
import ctr.ui.TextField;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class AmigosListEntity extends Entity {
    private static final int ROW_X = 90;
    private static final int ROW_Y = 190;
    private static final int ROW_W = 790;
    private static final int ROW_H = 68;
    private static final int MAX_ROWS = 6;

    private final Menu menus;
    private final SessionManager sessionManager;
    private final TextField txtUsername;
    private final Button btnAgregar;
    private final Button btnReto;
    private final Button btnVolver;
    private int selectedIndex = -1;
    private String mensaje = "";
    private int contadorMensaje;

    public AmigosListEntity(Scene scene, Menu menus, SessionManager sessionManager) {
        super(scene);
        this.menus = menus;
        this.sessionManager = sessionManager;

        txtUsername = new TextField(scene, 220, 35, 90, 120, I18n.t("friend_username"));
        btnAgregar = new Button(scene, I18n.t("add_friend"), 45, 28, 335, 105);
        btnReto = new Button(scene, I18n.t("challenge"), 28, 42, 275, 610);
        btnVolver = new Button(scene, I18n.t("back"), 50, 42, 555, 610);

        btnAgregar.setListener(() -> agregarAmigo());
        btnReto.setListener(() -> enviarReto());
        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
    }

    private void agregarAmigo() {
        mensaje = menus.agregarAmigo(txtUsername.getText());
        contadorMensaje = 150;
        txtUsername.clear();
        selectedIndex = -1;
    }

    private void enviarReto() {
        List<String> amigos = getAmigos();
        if (selectedIndex < 0 || selectedIndex >= amigos.size()) {
            mensaje = I18n.t("select_friend");
        } else {
            mensaje = menus.iniciarChallenge(amigos.get(selectedIndex));
        }
        contadorMensaje = 150;
    }

    @Override
    public void update() {
        if (!visible) return;
        txtUsername.update();
        btnAgregar.update();
        btnReto.update();
        btnVolver.update();
        updateSelection();
        if (contadorMensaje > 0) contadorMensaje--;
    }

    private void updateSelection() {
        if (!Mouse.pressed || Mouse.pressedConsumed) return;
        List<String> amigos = getAmigos();
        int rows = Math.min(MAX_ROWS, amigos.size());
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
        g.drawString(I18n.t("friends_title"), 420, 82);

        txtUsername.draw(g);
        btnAgregar.draw(g);

        List<String> amigos = getAmigos();
        if (amigos.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString(I18n.t("no_friends"), 375, 300);
        } else {
            drawFriendRows(g, amigos);
        }

        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(15f));
            g.drawString(mensaje, 90, 585);
        }

        btnReto.draw(g);
        btnVolver.draw(g);
    }

    private void drawFriendRows(Graphics2D g, List<String> amigos) {
        int rows = Math.min(MAX_ROWS, amigos.size());
        for (int i = 0; i < rows; i++) {
            String username = amigos.get(i);
            Usuario amigo = menus.cargarUsuario(username);
            int y = ROW_Y + i * ROW_H;

            g.setColor(i == selectedIndex ? new Color(255, 210, 90, 90) : new Color(255, 255, 255, 35));
            g.fillRoundRect(ROW_X, y, ROW_W, ROW_H - 8, 10, 10);

            if (amigo != null) {
                BufferedImage avatar = loadImageFromResource("/res/" + amigo.getAvatar());
                g.drawImage(avatar, ROW_X + 10, y + 7, 46, 46, null);
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(18f));
                g.drawString(shortName(amigo.getUsername()), ROW_X + 75, y + 28);
                g.setFont(g.getFont().deriveFont(15f));
                g.drawString(I18n.t("score") + ": " + amigo.getPuntuacionGeneral(), ROW_X + 290, y + 26);
                g.drawString(I18n.t("stars") + ": " + menus.totalEstrellas(amigo.getUsername()), ROW_X + 500, y + 26);
            } else {
                g.setColor(Color.WHITE);
                g.drawString(shortName(username), ROW_X + 75, y + 34);
            }
        }
    }

    private List<String> getAmigos() {
        Usuario usuario = sessionManager.getUsuarioActual();
        return usuario != null ? usuario.getAmigosRivales() : Collections.emptyList();
    }

    private String shortName(String username) {
        if (username == null) return "";
        return username.length() <= 16 ? username : username.substring(0, 13) + "...";
    }

    public TextField getTxtUsername() {
        return txtUsername;
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = newGameState == GameState.AMIGOS_LIST;
        if (visible) {
            refreshText();
            selectedIndex = -1;
            mensaje = "";
            contadorMensaje = 0;
            btnAgregar.reset();
            btnReto.reset();
            btnVolver.reset();
        }
    }

    private void refreshText() {
        txtUsername.setPlaceholder(I18n.t("friend_username"));
        btnAgregar.setText(I18n.t("add_friend"));
        btnReto.setText(I18n.t("challenge"));
        btnVolver.setText(I18n.t("back"));
    }
}
