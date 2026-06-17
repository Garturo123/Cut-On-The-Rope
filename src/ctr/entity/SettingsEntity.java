package ctr.entity;

import Usuarios.SessionManager;
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
    private final Button btnAudio;
    private final Button btnLanguage;
    private final Button btnAvatar;
    private final Button btnBack;

    public SettingsEntity(Scene scene, SessionManager sessionManager) {
        super(scene);
        this.sessionManager = sessionManager;

        btnAudio = new Button(scene, I18n.t("audio"), 55, 42, 415, 210);
        btnLanguage = new Button(scene, I18n.t("language"), 35, 42, 415, 300);
        btnAvatar = new Button(scene, I18n.t("avatar"), 55, 42, 415, 390);
        btnBack = new Button(scene, I18n.t("back"), 50, 42, 415, 520);

        btnAudio.setListener(() -> scene.cambiarAState(GameState.AUDIO_CONFIG));
        btnLanguage.setListener(() -> scene.cambiarAState(GameState.LANGUAGE));
        btnAvatar.setListener(() -> scene.cambiarAState(GameState.AVATAR_SELECTOR));
        btnBack.setListener(() -> scene.cambiarAState(sessionManager.haySesionActiva()
            ? GameState.MENU_SESION
            : GameState.MENU_PRINCIPAL));
    }

    @Override
    public void update() {
        if (!visible) return;
        btnAudio.update();
        btnLanguage.update();
        if (sessionManager.haySesionActiva()) {
            btnAvatar.update();
        }
        btnBack.update();
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
        }
        btnBack.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = newGameState == GameState.SETTINGS;
        if (visible) {
            refreshText();
            btnAudio.reset();
            btnLanguage.reset();
            btnAvatar.reset();
            btnBack.reset();
        }
    }

    private void refreshText() {
        btnAudio.setText(I18n.t("audio"));
        btnLanguage.setText(I18n.t("language"));
        btnAvatar.setText(I18n.t("avatar"));
        btnBack.setText(I18n.t("back"));
    }
}
