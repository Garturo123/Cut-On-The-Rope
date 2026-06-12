package ctr.entity;

import Usuarios.AuthService;
import Usuarios.SessionManager;
import Usuarios.UsuarioRepo;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import ctr.ui.TextField;
import java.awt.Color;
import java.awt.Graphics2D;

public class RegisterEntity extends Entity {
    
    private TextField txtUsername;
    private TextField txtNombreCompleto;
    private TextField txtPassword;
    private TextField txtConfirmPassword;
    private Button btnRegister;
    private Button btnBack;
    
    private String mensaje = "";
    private boolean mensajeError = true;
    private int contadorMensaje = 0;
    
    private AuthService authService;
    private UsuarioRepo usuarioRepo;
    private SessionManager sessionManager;
    
    public RegisterEntity(Scene scene, UsuarioRepo usuarioRepo, SessionManager sessionManager) {
        super(scene);
        this.usuarioRepo = usuarioRepo;
        this.sessionManager = sessionManager;
        this.authService = new AuthService(usuarioRepo, sessionManager);
        
        int ancho = 220;
        int x = 290;
        
        txtUsername = new TextField(scene, ancho, 35, x, 160, "Username");
        txtNombreCompleto = new TextField(scene, ancho, 35, x, 210, "Full Name");
        txtPassword = new TextField(scene, ancho, 35, x, 260, "Password", true);
        txtConfirmPassword = new TextField(scene, ancho, 35, x, 310, "Confirm Password", true);
        
        btnRegister = new Button(scene, "Register", 60, 28, 340, 380);
        btnBack = new Button(scene, "Back", 50, 28, 340, 430);
        
        btnRegister.setListener(() -> intentarRegistro());
        btnBack.setListener(() -> scene.cambiarAState(GameState.MENU_PRINCIPAL));
    }
    
    private void intentarRegistro() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String confirm = txtConfirmPassword.getText();
        String nombre = txtNombreCompleto.getText();
        
        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            mostrarMensaje("Complete all fields", true);
            return;
        }
        
        if (!password.equals(confirm)) {
            mostrarMensaje("Passwords do not match", true);
            return;
        }
        
        String resultado = authService.crearUsuario(username, password, nombre);
        
        if (resultado.equals("Usuario creado correctamente")) {
            mostrarMensaje(resultado, false);
            authService.login(username, password);
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
        if (!visible) return;
        
        txtUsername.update();
        txtNombreCompleto.update();
        txtPassword.update();
        txtConfirmPassword.update();
        btnRegister.update();
        btnBack.update();
        
        if (contadorMensaje > 0) {
            contadorMensaje--;
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(32f));
        g.drawString("REGISTER", 340, 110);
        
        txtUsername.draw(g);
        txtNombreCompleto.draw(g);
        txtPassword.draw(g);
        txtConfirmPassword.draw(g);
        btnRegister.draw(g);
        btnBack.draw(g);
        
        if (contadorMensaje > 0) {
            g.setColor(mensajeError ? Color.RED : Color.GREEN);
            g.setFont(g.getFont().deriveFont(13f));
            int x = 400 - (g.getFontMetrics().stringWidth(mensaje) / 2);
            g.drawString(mensaje, x, 520);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.REGISTER);
        if (visible) {
            txtUsername.clear();
            txtNombreCompleto.clear();
            txtPassword.clear();
            txtConfirmPassword.clear();
            mensaje = "";
            contadorMensaje = 0;
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}