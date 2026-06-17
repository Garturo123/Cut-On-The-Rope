package ctr.entity;

import ctr.Entity;
import ctr.I18n;
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
    private Button btnSettings;
    private Button btnExit;
    private boolean loginPressed = false;
    private boolean registerPressed = false;
    private boolean settingsPressed = false;
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
        btnLogin = new Button(scene, I18n.t("login"), 50, 42, 415, 250);
        btnRegister = new Button(scene, I18n.t("register"), 30, 42, 415, 330);
        btnSettings = new Button(scene, I18n.t("settings"), 30, 42, 415, 410);
        btnExit = new Button(scene, I18n.t("exit"), 60, 42, 415, 490);
        
        // Listeners
        btnLogin.setListener(() -> loginPressed = true);
        btnRegister.setListener(() -> registerPressed = true);
        btnSettings.setListener(() -> settingsPressed = true);
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
                btnSettings.setListener(() -> settingsPressed = true);
                btnExit.setListener(() -> exitPressed = true);

                instructionPointer = 2;

            case 2:
                btnLogin.update();
                btnRegister.update();
                btnSettings.update();
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
                else if (settingsPressed)
                {
                    scene.cambiarAState(GameState.SETTINGS);
                    settingsPressed = false;
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
        g.drawImage(image, 0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT, null);
        titleShadowTransform.setToIdentity();
        titleShadowTransform.translate(View.SCREEN_WIDTH / 2, View.SCREEN_HEIGHT / 2);
        titleShadowTransform.rotate(titleShadowAngle);
        titleShadowTransform.translate(-titleShadow.getWidth() / 2, -titleShadow.getHeight() / 2);
        g.drawImage(titleShadow, titleShadowTransform, null);
        g.drawImage(title, (View.SCREEN_WIDTH - title.getWidth()) / 2, 70, null);
       if (btnLogin.isVisible())
            btnLogin.draw(g);

        if (btnRegister.isVisible())
            btnRegister.draw(g);

        if (btnSettings.isVisible())
            btnSettings.draw(g);

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
            settingsPressed = false;
            exitPressed = false;

            refreshText();
            btnLogin.setListener(null);
            btnRegister.setListener(null);
            btnSettings.setListener(null);
            btnExit.setListener(null);

            btnLogin.reset();
            btnRegister.reset();
            btnSettings.reset();
            btnExit.reset();
        }
    }

    private void refreshText() {
        btnLogin.setText(I18n.t("login"));
        btnRegister.setText(I18n.t("register"));
        btnSettings.setText(I18n.t("settings"));
        btnExit.setText(I18n.t("exit"));
    }
}
