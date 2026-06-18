package ctr.entity;

import Usuarios.SessionManager;
import Usuarios.Usuario;
import Usuarios.UsuarioRepo;
import ctr.Entity;
import ctr.I18n;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;

public class SettingsEntity extends Entity {
    private final SessionManager sessionManager;
    private final UsuarioRepo repo;
    private final Button btnAudio;
    private final Button btnLanguage;
    private final Button btnAvatar;
    private final Button btnDeactivate;
    private final Button btnBack;
    private String mensaje = "";
    private int contadorMensaje;
    private boolean confirmarDesactivar;

    public SettingsEntity(Scene scene, SessionManager sessionManager, UsuarioRepo repo) {
        super(scene);
        this.sessionManager = sessionManager;
        this.repo = repo;

        btnAudio = new Button(scene, I18n.t("audio"), 55, 42, 415, 210);
        btnLanguage = new Button(scene, I18n.t("language"), 35, 42, 415, 300);
        btnAvatar = new Button(scene, I18n.t("avatar"), 55, 42, 415, 390);
        btnDeactivate = new Button(scene, I18n.t("deactivate_account"), 25, 42, 415, 480);
        btnBack = new Button(scene, I18n.t("back"), 50, 42, 415, 610);

        btnAudio.setListener(() -> scene.cambiarAState(GameState.AUDIO_CONFIG));
        btnLanguage.setListener(() -> scene.cambiarAState(GameState.LANGUAGE));
        btnAvatar.setListener(() -> scene.cambiarAState(GameState.AVATAR_SELECTOR));
        btnDeactivate.setListener(() -> desactivarCuenta());
        btnBack.setListener(() -> scene.cambiarAState(sessionManager.haySesionActiva()
            ? GameState.MENU_SESION
            : GameState.MENU_PRINCIPAL));
    }

    private void desactivarCuenta() {
        Usuario usuario = sessionManager.getUsuarioActual();
        if (usuario == null) return;

        if (!confirmarDesactivar) {
            confirmarDesactivar = true;
            mensaje = I18n.t("confirm_deactivate");
            contadorMensaje = 180;
            return;
        }

        usuario.desactivarCuenta();
        repo.guardar(usuario);
        sessionManager.cerrarSesion();
        mensaje = I18n.t("account_deactivated");
        contadorMensaje = 60;
        confirmarDesactivar = false;
        scene.cambiarAState(GameState.MENU_PRINCIPAL);
    }

    @Override
    public void update() {
        if (!visible) return;
        btnAudio.update();
        btnLanguage.update();
        if (sessionManager.haySesionActiva()) {
            btnAvatar.update();
            btnDeactivate.update();
        }
        btnBack.update();
        if (contadorMensaje > 0) {
            contadorMensaje--;
        } else {
            confirmarDesactivar = false;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setColor(new Color(0, 0, 0, 215));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(32f));
        g.drawString(I18n.t("settings_title"), 405, 115);

        btnAudio.draw(g);
        btnLanguage.draw(g);
        if (sessionManager.haySesionActiva()) {
            btnAvatar.draw(g);
            btnDeactivate.draw(g);
        }
        btnBack.draw(g);

        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(16f));
            g.drawString(mensaje, 405, 590);
        }
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = newGameState == GameState.SETTINGS;
        if (visible) {
            refreshText();
            btnAudio.reset();
            btnLanguage.reset();
            btnAvatar.reset();
            btnDeactivate.reset();
            btnBack.reset();
            confirmarDesactivar = false;
            contadorMensaje = 0;
        }
    }

    private void refreshText() {
        btnAudio.setText(I18n.t("audio"));
        btnLanguage.setText(I18n.t("language"));
        btnAvatar.setText(I18n.t("avatar"));
        btnDeactivate.setText(I18n.t("deactivate_account"));
        btnBack.setText(I18n.t("back"));
    }
}
