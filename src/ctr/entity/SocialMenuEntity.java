package ctr.entity;

import Usuarios.SessionManager;
import Usuarios.Usuario;
import ctr.Entity;
import ctr.I18n;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SocialMenuEntity extends Entity {
    private final SessionManager sessionManager;
    private final Button btnPerfil;
    private final Button btnAmigos;
    private final Button btnRetos;
    private final Button btnStats;
    private final Button btnVolver;
    private final BufferedImage title;
    private final BufferedImage titleShadow;
    private final AffineTransform titleShadowTransform = new AffineTransform();
    private double titleShadowAngle = Math.toRadians(45);

    public SocialMenuEntity(Scene scene, SessionManager sessionManager) {
        super(scene);
        this.sessionManager = sessionManager;

        title = loadImageFromResource("/res/title.png");
        titleShadow = loadImageFromResource("/res/title_shadow.png");
        loadImageFromResource("/res/title_background.png");

        btnPerfil = new Button(scene, I18n.t("profile"), 45, 42, 415, 240);
        btnAmigos = new Button(scene, I18n.t("friends"), 45, 42, 415, 325);
        btnRetos = new Button(scene, I18n.t("world_ranking"), 20, 42, 415, 410);
        btnStats = new Button(scene, I18n.t("stats"), 60, 42, 415, 495);
        btnVolver = new Button(scene, I18n.t("back"), 50, 42, 415, 590);

        btnPerfil.setListener(() -> scene.cambiarAState(GameState.PERFIL));
        btnAmigos.setListener(() -> scene.cambiarAState(GameState.AMIGOS_LIST));
        btnRetos.setListener(() -> scene.cambiarAState(GameState.CHALLENGE_SELECT));
        btnStats.setListener(() -> scene.cambiarAState(GameState.STATS));
        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
    }

    @Override
    protected void updateSocialMenu() {
        if (!visible) return;

        titleShadowAngle += 0.0025;
        btnPerfil.update();
        btnAmigos.update();
        btnRetos.update();
        btnStats.update();
        btnVolver.update();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;

        g.drawImage(image, 0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT, null);
        titleShadowTransform.setToIdentity();
        titleShadowTransform.translate(View.SCREEN_WIDTH / 2, View.SCREEN_HEIGHT / 2);
        titleShadowTransform.rotate(titleShadowAngle);
        titleShadowTransform.translate(-titleShadow.getWidth() / 2, -titleShadow.getHeight() / 2);
        g.drawImage(titleShadow, titleShadowTransform, null);
        g.drawImage(title, (View.SCREEN_WIDTH - title.getWidth()) / 2, 45, null);

        Usuario usuario = sessionManager.getUsuarioActual();
        String nombre = usuario != null ? usuario.getNombreCompleto() : "Player";
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(22f));
        g.drawString(I18n.t("social_menu") + " - " + nombre, 390, 205);

        btnPerfil.draw(g);
        btnAmigos.draw(g);
        btnRetos.draw(g);
        btnStats.draw(g);
        btnVolver.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = newGameState == GameState.SOCIAL_MENU;
        if (visible) {
            refreshText();
            btnPerfil.reset();
            btnAmigos.reset();
            btnRetos.reset();
            btnStats.reset();
            btnVolver.reset();
        }
    }

    private void refreshText() {
        btnPerfil.setText(I18n.t("profile"));
        btnAmigos.setText(I18n.t("friends"));
        btnRetos.setText(I18n.t("world_ranking"));
        btnStats.setText(I18n.t("stats"));
        btnVolver.setText(I18n.t("back"));
    }
}
