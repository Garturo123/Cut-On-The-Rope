package ctr.entity;

import Usuarios.AuthService;
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MenuSesionEntity extends Entity {

    private final SessionManager sessionManager;
    private final AuthService authService;
    private final Button btnPlay;
    private final Button btnPerfil;
    private final Button btnSettings;
    private final Button btnAmigos;
    private final Button btnRetos;
    private final Button btnStats;
    private final Button btnLogout;
    private final BufferedImage title;
    private final BufferedImage titleShadow;
    private final AffineTransform titleShadowTransform = new AffineTransform();
    private double titleShadowAngle = Math.toRadians(45);

    public MenuSesionEntity(Scene scene, UsuarioRepo usuarioRepo, SessionManager sessionManager) {
        super(scene);
        this.sessionManager = sessionManager;
        this.authService = new AuthService(usuarioRepo, sessionManager);

        title = loadImageFromResource("/res/title.png");
        titleShadow = loadImageFromResource("/res/title_shadow.png");
        loadImageFromResource("/res/title_background.png");

        btnPlay = new Button(scene, I18n.t("play"), 60, 42, 415, 220);
        btnPerfil = new Button(scene, I18n.t("profile"), 45, 42, 415, 290);
        btnAmigos = new Button(scene, I18n.t("friends"), 45, 42, 415, 360);
        btnRetos = new Button(scene, I18n.t("challenges"), 20, 42, 415, 430);
        btnStats = new Button(scene, I18n.t("stats"), 60, 42, 415, 500);
        btnSettings = new Button(scene, I18n.t("settings"), 30, 42, 275, 590);
        btnLogout = new Button(scene, I18n.t("logout"), 50, 42, 555, 590);

        btnPlay.setListener(() -> scene.cambiarAState(GameState.LEVEL_SELECT));
        btnPerfil.setListener(() -> scene.cambiarAState(GameState.PERFIL));
        btnAmigos.setListener(() -> scene.cambiarAState(GameState.AMIGOS_LIST));
        btnRetos.setListener(() -> scene.cambiarAState(GameState.CHALLENGE_SELECT));
        btnStats.setListener(() -> scene.cambiarAState(GameState.STATS));
        btnSettings.setListener(() -> scene.cambiarAState(GameState.SETTINGS));
        btnLogout.setListener(() -> {
            authService.logout();
            scene.cambiarAState(GameState.MENU_PRINCIPAL);
        });
    }

    @Override
    protected void updateMenuSesion() {
        if (!visible) return;

        titleShadowAngle += 0.0025;
        btnPlay.update();
        btnPerfil.update();
        btnAmigos.update();
        btnRetos.update();
        btnStats.update();
        btnSettings.update();
        btnLogout.update();
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
        g.drawImage(title, (View.SCREEN_WIDTH - title.getWidth()) / 2, 50, null);

        Usuario usuario = sessionManager.getUsuarioActual();
        String nombre = usuario != null ? usuario.getNombreCompleto() : "Player";
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString(I18n.t("menu_welcome") + nombre, 415, 200);

        btnPlay.draw(g);
        btnPerfil.draw(g);
        btnAmigos.draw(g);
        btnRetos.draw(g);
        btnStats.draw(g);
        btnSettings.draw(g);
        btnLogout.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.MENU_SESION);
        if (visible) {
            refreshText();
            btnPlay.reset();
            btnPerfil.reset();
            btnAmigos.reset();
            btnRetos.reset();
            btnStats.reset();
            btnSettings.reset();
            btnLogout.reset();
        }
    }

    private void refreshText() {
        btnPlay.setText(I18n.t("play"));
        btnPerfil.setText(I18n.t("profile"));
        btnAmigos.setText(I18n.t("friends"));
        btnRetos.setText(I18n.t("challenges"));
        btnStats.setText(I18n.t("stats"));
        btnSettings.setText(I18n.t("settings"));
        btnLogout.setText(I18n.t("logout"));
    }
}
