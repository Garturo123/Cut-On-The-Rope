package ctr.entity;

import Usuarios.Menu;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import ctr.ui.TextField;
import java.awt.Color;
import java.awt.Graphics2D;

public class ReactivateAccountEntity extends Entity {
    
    private TextField txtUsername;
    private TextField txtPassword;
    private Button btnReactivar;
    private Button btnBack;
    
    private String mensaje = "";
    private boolean mensajeError = true;
    private int contadorMensaje = 0;
    
    private transient Menu menus;
    
    public ReactivateAccountEntity(Scene scene, Menu smenus) {
        super(scene);
        this.menus = menus;
        
        txtUsername = new TextField(scene, 200, 35, 300, 220, "Username");
        txtPassword = new TextField(scene, 200, 35, 300, 280, "Password", true);
        
        btnReactivar = new Button(scene, "Reactivate", 70, 28, 340, 350);
        btnBack = new Button(scene, "Back", 50, 28, 340, 400);
        
        btnReactivar.setListener(() -> intentarReactivar());
        btnBack.setListener(() -> scene.cambiarAState(GameState.LOGIN));
    }
    
    private void intentarReactivar() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Enter username and password", true);
            return;
        }
        
        String resultado = menus.reactivarCuenta(username, password);
        
        if (resultado.contains("successfully")) {
            mostrarMensaje(resultado, false);
            scene.cambiarAState(GameState.LEVEL_SELECT);
        } else {
            mostrarMensaje(resultado, true);
        }
    }
    
    private void mostrarMensaje(String msg, boolean error) {
        mensaje = msg;
        mensajeError = error;
        contadorMensaje = 180;
    }
    
    @Override
    public void update() {
        txtUsername.update();
        txtPassword.update();
        btnReactivar.update();
        btnBack.update();
        
        if (contadorMensaje > 0) {
            contadorMensaje--;
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(28f));
        g.drawString("REACTIVATE ACCOUNT", 290, 150);
        
        g.setFont(g.getFont().deriveFont(14f));
        g.setColor(Color.YELLOW);
        g.drawString("Your account is disabled. Enter your credentials to reactivate.", 260, 190);
        
        txtUsername.draw(g);
        txtPassword.draw(g);
        btnReactivar.draw(g);
        btnBack.draw(g);
        
        if (contadorMensaje > 0) {
            g.setColor(mensajeError ? Color.RED : Color.GREEN);
            g.setFont(g.getFont().deriveFont(13f));
            int x = 400 - (mensaje.length() * 4) / 2;
            g.drawString(mensaje, x, 490);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.REACTIVATE_ACCOUNT);
        if (visible) {
            txtUsername.clear();
            txtPassword.clear();
            mensaje = "";
            contadorMensaje = 0;
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}