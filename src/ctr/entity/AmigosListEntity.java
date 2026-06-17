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
    private static final int MAX_ROWS = 4;

    private final Menu menus;
    private final SessionManager sessionManager;
    private final TextField txtUsername;
    private final Button btnAgregar;
    private final Button btnAceptar;
    private final Button btnReto;
    private final Button btnVolver;
    private int selectedIndex = -1;
    private boolean selectedRequest;
    private String mensaje = "";
    private int contadorMensaje;

    public AmigosListEntity(Scene scene, Menu menus, SessionManager sessionManager) {
        super(scene);
        this.menus = menus;
        this.sessionManager = sessionManager;

        txtUsername = new TextField(scene, 220, 35, 90, 120, I18n.t("friend_username"));
        btnAgregar = new Button(scene, I18n.t("add_friend"), 45, 28, 335, 105);
        btnAceptar = new Button(scene, I18n.t("accept"), 45, 42, 180, 610);
        btnReto = new Button(scene, I18n.t("challenge"), 28, 42, 410, 610);
        btnVolver = new Button(scene, I18n.t("back"), 50, 42, 640, 610);

        btnAgregar.setListener(() -> agregarAmigo());
        btnAceptar.setListener(() -> aceptarSolicitud());
        btnReto.setListener(() -> enviarReto());
        btnVolver.setListener(() -> scene.cambiarAState(GameState.SOCIAL_MENU));
    }

    private void agregarAmigo() {
        mensaje = menus.agregarAmigo(txtUsername.getText());
        contadorMensaje = 150;
        txtUsername.clear();
        selectedIndex = -1;
        selectedRequest = false;
    }

    private void aceptarSolicitud() {
        List<String> solicitudes = getSolicitudes();
        if (!selectedRequest || selectedIndex < 0 || selectedIndex >= solicitudes.size()) {
            mensaje = I18n.t("select_request");
        } else {
            mensaje = menus.aceptarSolicitudAmistad(solicitudes.get(selectedIndex));
        }
        contadorMensaje = 150;
        selectedIndex = -1;
        selectedRequest = false;
    }

    private void enviarReto() {
        List<String> amigos = getAmigos();
        if (selectedRequest || selectedIndex < 0 || selectedIndex >= amigos.size()) {
            mensaje = I18n.t("select_friend");
        } else {
            mensaje = menus.iniciarChallenge(amigos.get(selectedIndex));
            if ("Challenge iniciado".equals(mensaje)) {
                scene.startFriendlyChallenge(amigos.get(selectedIndex));
            }
        }
        contadorMensaje = 150;
    }

    @Override
    public void update() {
        if (!visible) return;
        txtUsername.update();
        btnAgregar.update();
        btnAceptar.update();
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
                selectedRequest = false;
                Mouse.pressedConsumed = true;
                return;
            }
        }

        List<String> solicitudes = getSolicitudes();
        int requestY = ROW_Y + MAX_ROWS * ROW_H + 12;
        int requestRows = Math.min(2, solicitudes.size());
        for (int i = 0; i < requestRows; i++) {
            int y = requestY + i * ROW_H;
            if (Mouse.x >= ROW_X && Mouse.x <= ROW_X + ROW_W && Mouse.y >= y && Mouse.y <= y + ROW_H - 8) {
                selectedIndex = i;
                selectedRequest = true;
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

        drawRequestRows(g, getSolicitudes());

        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(15f));
            g.drawString(mensaje, 90, 585);
        }

        btnAceptar.draw(g);
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
                g.drawString(I18n.t("levels_completed") + menus.nivelesCompletados(amigo.getUsername()), ROW_X + 290, y + 26);
                g.drawString(I18n.t("stars") + ": " + menus.totalEstrellas(amigo.getUsername()), ROW_X + 560, y + 26);
            } else {
                g.setColor(Color.WHITE);
                g.drawString(shortName(username), ROW_X + 75, y + 34);
            }
        }
    }

    private void drawRequestRows(Graphics2D g, List<String> solicitudes) {
        int titleY = ROW_Y + MAX_ROWS * ROW_H;
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(17f));
        g.drawString(I18n.t("friend_requests"), ROW_X, titleY);

        if (solicitudes.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(g.getFont().deriveFont(14f));
            g.drawString(I18n.t("no_requests"), ROW_X + 170, titleY);
            return;
        }

        int rows = Math.min(2, solicitudes.size());
        int startY = titleY + 12;
        for (int i = 0; i < rows; i++) {
            String username = solicitudes.get(i);
            int y = startY + i * ROW_H;
            g.setColor(selectedRequest && i == selectedIndex ? new Color(255, 210, 90, 90) : new Color(90, 160, 255, 45));
            g.fillRoundRect(ROW_X, y, ROW_W, ROW_H - 8, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(18f));
            g.drawString(shortName(username), ROW_X + 20, y + 34);
            g.setFont(g.getFont().deriveFont(15f));
            g.drawString(I18n.t("pending_request"), ROW_X + 290, y + 34);
        }
    }

    private List<String> getAmigos() {
        Usuario usuario = sessionManager.getUsuarioActual();
        return usuario != null ? usuario.getAmigosRivales() : Collections.emptyList();
    }

    private List<String> getSolicitudes() {
        Usuario usuario = sessionManager.getUsuarioActual();
        return usuario != null ? usuario.getSolicitudesAmistadRecibidas() : Collections.emptyList();
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
            selectedRequest = false;
            mensaje = "";
            contadorMensaje = 0;
            btnAgregar.reset();
            btnAceptar.reset();
            btnReto.reset();
            btnVolver.reset();
        }
    }

    private void refreshText() {
        txtUsername.setPlaceholder(I18n.t("friend_username"));
        btnAgregar.setText(I18n.t("add_friend"));
        btnAceptar.setText(I18n.t("accept"));
        btnReto.setText(I18n.t("challenge"));
        btnVolver.setText(I18n.t("back"));
    }
}
