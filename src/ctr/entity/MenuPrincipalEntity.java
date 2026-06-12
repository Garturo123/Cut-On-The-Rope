package ctr.entity;

import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MenuPrincipalEntity extends Entity {
    
    private Button btnLogin;
    private Button btnRegister;
    private Button btnExit;
    private boolean loginPressed = false;
    private boolean registerPressed = false;
    private boolean exitPressed = false;
    
    private BufferedImage background;
    private BufferedImage logoTitulo;
    
    public MenuPrincipalEntity(Scene scene) {
        super(scene);
        
        // Cargar imágenes
        background = loadImageFromResource("/res/menu_bg.png");
        logoTitulo = loadImageFromResource("/res/logo.png");
        
        // Crear botones con tus texturas personalizadas
        btnLogin = new Button(scene, "Login", 120, 50, 300, 250);
        btnRegister = new Button(scene, "Register", 120, 50, 300, 320);
        btnExit = new Button(scene, "Exit", 120, 50, 300, 390);
        
        // Listeners
        btnLogin.setListener(() -> loginPressed = true);
        btnRegister.setListener(() -> registerPressed = true);
        btnExit.setListener(() -> exitPressed = true);
    }
    
    @Override
    protected void updateFixedLevelCleared() {
        // No usado en esta entidad
    }
    
    @Override
    protected void updateLevelCleared() {
        // Método de actualización normal
        btnLogin.update();
        btnRegister.update();
        btnExit.update();
        
        if (loginPressed) {
            scene.cambiarAState(GameState.LOGIN);
            loginPressed = false;
        } else if (registerPressed) {
            scene.cambiarAState(GameState.REGISTER);
            registerPressed = false;
        } else if (exitPressed) {
            System.exit(0);
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, 800, 600, null);
        }
        if (logoTitulo != null) {
            g.drawImage(logoTitulo, 300, 80, 200, 80, null);
        }
        
        btnLogin.draw(g);
        btnRegister.draw(g);
        btnExit.draw(g);
    }
    
    
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.MENU_PRINCIPAL);
        if (visible) {
            loginPressed = false;
            registerPressed = false;
            exitPressed = false;
        }
    }
}
