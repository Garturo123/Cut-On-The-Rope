package ctr.entity;

import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import ctr.ui.ButtonListener;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MenuPrincipalEntity extends Entity {
    
    private Button btnLogin;
    private Button btnRegister;
    private Button btnExit;
    private boolean loginPressed = false;
    private boolean registerPressed = false;
    private boolean exitPressed = false;
    private AffineTransform titleShadowTransform = new AffineTransform();
    private double titleShadowAngle = Math.toRadians(45);
    private Button button;
    private ButtonListener buttonListener;
    private BufferedImage title;
    private BufferedImage titleShadow;
    
    public MenuPrincipalEntity(Scene scene) {
        super(scene);
        
        // Cargar imágenes
        title = loadImageFromResource("/res/title.png");
        titleShadow = loadImageFromResource("/res/title_shadow.png");
        loadImageFromResource("/res/title_background.png");
        
        // Crear botones con tus texturas personalizadas
        btnLogin = new Button(scene, "Login", 50, 42, 300, 250);
        btnRegister = new Button(scene, "Register", 30, 42, 300, 320);
        btnExit = new Button(scene, "Exit", 60, 42, 300, 390);
        
        // Listeners
        btnLogin.setListener(() -> loginPressed = true);
        btnRegister.setListener(() -> registerPressed = true);
        btnExit.setListener(() -> exitPressed = true);
    }
    
    
    @Override
    protected void updateMenuPrincipal()
    {
        titleShadowAngle += 0.0025;

        switch (instructionPointer)
        {
            case 0:
                setCurrentWaitTime();
                instructionPointer = 1;

            case 1:
                if (!checkPassedTime(0.5))
                    return;

                btnLogin.setListener(() -> loginPressed = true);
                btnRegister.setListener(() -> registerPressed = true);
                btnExit.setListener(() -> exitPressed = true);

                instructionPointer = 2;

            case 2:
                btnLogin.update();
                btnRegister.update();
                btnExit.update();

                if (loginPressed)
                {
                    scene.cambiarAState(GameState.LOGIN);
                    loginPressed = false;
                }
                else if (registerPressed)
                {
                    scene.cambiarAState(GameState.REGISTER);
                    registerPressed = false;
                }
                else if (exitPressed)
                {
                    System.exit(0);
                }

                return;
        }


    }
    
    @Override
    public void draw(Graphics2D g) {
        g.drawImage(image, 0, 0, null);
        titleShadowTransform.setToIdentity();
        titleShadowTransform.translate(View.SCREEN_WIDTH / 2, View.SCREEN_HEIGHT / 2);
        titleShadowTransform.rotate(titleShadowAngle);
        titleShadowTransform.translate(-titleShadow.getWidth() / 2, -titleShadow.getHeight() / 2);
        g.drawImage(titleShadow, titleShadowTransform, null);
        g.drawImage(title, 180, 60, null);
       if (btnLogin.isVisible())
            btnLogin.draw(g);

        if (btnRegister.isVisible())
            btnRegister.draw(g);

        if (btnExit.isVisible())
            btnExit.draw(g);
    }
    
    
    public void gameStateChanged(GameState newGameState) {
        visible = false;

        if (newGameState == GameState.MENU_PRINCIPAL)
        {
            visible = true;
            instructionPointer = 0;

            loginPressed = false;
            registerPressed = false;
            exitPressed = false;

            btnLogin.setListener(null);
            btnRegister.setListener(null);
            btnExit.setListener(null);

            btnLogin.reset();
            btnRegister.reset();
            btnExit.reset();
        }
    }
}
