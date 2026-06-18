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
    private final Button btnSocial;
    private final Button btnSettings;
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
        btnSocial = new Button(scene, I18n.t("social_menu"), 25, 42, 415, 320);
        btnSettings = new Button(scene, I18n.t("settings"), 30, 42, 415, 420);
        btnLogout = new Button(scene, I18n.t("logout"), 50, 42, 415, 520);

        btnPlay.setListener(() -> scene.cambiarAState(GameState.LEVEL_SELECT));
        btnSocial.setListener(() -> scene.cambiarAState(GameState.SOCIAL_MENU));
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
        btnSocial.update();
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
        btnSocial.draw(g);
        btnSettings.draw(g);
        btnLogout.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.MENU_SESION);
        if (visible) {
            refreshText();
            btnPlay.reset();
            btnSocial.reset();
            btnSettings.reset();
            btnLogout.reset();
        }
    }

    private void refreshText() {
        btnPlay.setText(I18n.t("play"));
        btnSocial.setText(I18n.t("social_menu"));
        btnSettings.setText(I18n.t("settings"));
        btnLogout.setText(I18n.t("logout"));
    }
}
