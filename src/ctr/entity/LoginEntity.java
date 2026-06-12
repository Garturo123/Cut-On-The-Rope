package ctr.entity;

import Usuarios.Menu;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import ctr.ui.TextField;
import java.awt.Color;
import java.awt.Graphics2D;

public class LoginEntity extends Entity {
    
    private TextField txtUsername;
    private TextField txtPassword;
    private Button btnLogin;
    private Button btnBack;
    private Button btnReactivar;
    private String mensajeError = "";
    private int contadorError = 0;
    
    private transient Menu menus;
    
    public LoginEntity(Scene scene, Menu menus) {
        super(scene);
        this.menus = menus;
        
        txtUsername = new TextField(scene, 200, 35, 300, 220, "Username");
        txtPassword = new TextField(scene, 200, 35, 300, 280, "Password", true);
        
        btnLogin = new Button(scene, "Login", 50, 28, 340, 350);
        btnBack = new Button(scene, "Back", 50, 28, 340, 400);
        btnReactivar = new Button(scene, "Reactivate", 65, 28, 340, 450);
        
        btnLogin.setListener(() -> intentarLogin());
        btnBack.setListener(() -> scene.cambiarAState(GameState.MENU_PRINCIPAL));
        btnReactivar.setListener(() -> scene.cambiarAState(GameState.REACTIVATE_ACCOUNT));
    }
    
    private void intentarLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        String resultado = menus.login(username, password);
        
        if (resultado.equals("Welcome")) {
            scene.cambiarAState(GameState.LEVEL_SELECT);
        } else {
            mensajeError = resultado;
            contadorError = 180;
        }
    }
    
    @Override
    public void update() {
        txtUsername.update();
        txtPassword.update();
        btnLogin.update();
        btnBack.update();
        btnReactivar.update();
        
        if (contadorError > 0) {
            contadorError--;
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        // Fondo
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        // Título
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(36f));
        g.drawString("LOGIN", 370, 150);
        
        // Campos
        txtUsername.draw(g);
        txtPassword.draw(g);
        btnLogin.draw(g);
        btnBack.draw(g);
        btnReactivar.draw(g);
        
        // Mensaje de error
        if (contadorError > 0) {
            g.setColor(Color.RED);
            g.setFont(g.getFont().deriveFont(14f));
            g.drawString(mensajeError, 310, 340);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.LOGIN);
        if (visible) {
            txtUsername.clear();
            txtPassword.clear();
            mensajeError = "";
            contadorError = 0;
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}