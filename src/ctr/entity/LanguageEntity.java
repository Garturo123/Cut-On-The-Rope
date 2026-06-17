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

public class LanguageEntity extends Entity {
    private final SessionManager sessionManager;
    private final UsuarioRepo usuarioRepo;
    private final Button btnEnglish;
    private final Button btnSpanish;
    private final Button btnBack;

    public LanguageEntity(Scene scene, SessionManager sessionManager, UsuarioRepo usuarioRepo) {
        super(scene);
        this.sessionManager = sessionManager;
        this.usuarioRepo = usuarioRepo;

        btnEnglish = new Button(scene, I18n.t("english"), 35, 42, 300, 260);
        btnSpanish = new Button(scene, I18n.t("spanish"), 35, 42, 530, 260);
        btnBack = new Button(scene, I18n.t("back"), 50, 42, 415, 430);

        btnEnglish.setListener(() -> changeLanguage("English"));
        btnSpanish.setListener(() -> changeLanguage("Spanish"));
        btnBack.setListener(() -> scene.cambiarAState(GameState.SETTINGS));
    }

    private void changeLanguage(String language) {
        I18n.setLanguage(language);
        Usuario usuario = sessionManager.getUsuarioActual();
        if (usuario != null) {
            usuario.setIdioma(I18n.getLanguage());
            usuarioRepo.guardar(usuario);
        }
        refreshText();
    }

    @Override
    public void update() {
        if (!visible) return;
        btnEnglish.update();
        btnSpanish.update();
        btnBack.update();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setColor(new Color(0, 0, 0, 215));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(32f));
        g.drawString(I18n.t("language_title"), 425, 130);

        g.setFont(g.getFont().deriveFont(16f));
        g.setColor(I18n.isSpanish() ? Color.LIGHT_GRAY : Color.YELLOW);
        g.drawString(I18n.isSpanish() ? "" : "*", 375, 245);
        g.setColor(I18n.isSpanish() ? Color.YELLOW : Color.LIGHT_GRAY);
        g.drawString(I18n.isSpanish() ? "*" : "", 605, 245);

        btnEnglish.draw(g);
        btnSpanish.draw(g);
        btnBack.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = newGameState == GameState.LANGUAGE;
        if (visible) {
            refreshText();
            btnEnglish.reset();
            btnSpanish.reset();
            btnBack.reset();
        }
    }

    private void refreshText() {
        btnEnglish.setText(I18n.t("english"));
        btnSpanish.setText(I18n.t("spanish"));
        btnBack.setText(I18n.t("back"));
    }
}
