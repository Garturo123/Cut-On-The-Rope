package ctr.entity;

import Usuarios.AuthService;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import Usuarios.UsuarioRepo;
import ctr.Entity;
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
    private final Button btnAudio;
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

        btnPlay = new Button(scene, "Play", 60, 42, 315, 225);
        btnPerfil = new Button(scene, "Profile", 45, 42, 315, 280);
        btnAudio = new Button(scene, "Audio", 55, 42, 315, 335);
        btnAmigos = new Button(scene, "Friends", 45, 42, 315, 390);
        btnRetos = new Button(scene, "Challenges", 20, 42, 315, 445);
        btnStats = new Button(scene, "Stats", 60, 42, 130, 500);
        btnLogout = new Button(scene, "Logout", 50, 42, 500, 500);

        btnPlay.setListener(() -> scene.cambiarAState(GameState.LEVEL_SELECT));
        btnPerfil.setListener(() -> scene.cambiarAState(GameState.PERFIL));
        btnAudio.setListener(() -> scene.cambiarAState(GameState.AUDIO_CONFIG));
        btnAmigos.setListener(() -> scene.cambiarAState(GameState.AMIGOS_LIST));
        btnRetos.setListener(() -> scene.cambiarAState(GameState.CHALLENGE_SELECT));
        btnStats.setListener(() -> scene.cambiarAState(GameState.STATS));
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
        btnAudio.update();
        btnAmigos.update();
        btnRetos.update();
        btnStats.update();
        btnLogout.update();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;

        g.drawImage(image, 0, 0, null);
        titleShadowTransform.setToIdentity();
        titleShadowTransform.translate(View.SCREEN_WIDTH / 2, View.SCREEN_HEIGHT / 2);
        titleShadowTransform.rotate(titleShadowAngle);
        titleShadowTransform.translate(-titleShadow.getWidth() / 2, -titleShadow.getHeight() / 2);
        g.drawImage(titleShadow, titleShadowTransform, null);
        g.drawImage(title, 180, 55, null);

        Usuario usuario = sessionManager.getUsuarioActual();
        String nombre = usuario != null ? usuario.getNombreCompleto() : "Player";
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString("Welcome, " + nombre, 315, 205);

        btnPlay.draw(g);
        btnPerfil.draw(g);
        btnAudio.draw(g);
        btnAmigos.draw(g);
        btnRetos.draw(g);
        btnStats.draw(g);
        btnLogout.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.MENU_SESION);
        if (visible) {
            btnPlay.reset();
            btnPerfil.reset();
            btnAudio.reset();
            btnAmigos.reset();
            btnRetos.reset();
            btnStats.reset();
            btnLogout.reset();
        }
    }
}