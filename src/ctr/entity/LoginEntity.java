package ctr.entity;

import Usuarios.AuthService;
import Usuarios.SessionManager;
import Usuarios.UsuarioRepo;
import ctr.Entity;
import ctr.I18n;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
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
    
    private AuthService authService;
    private UsuarioRepo usuarioRepo;
    private SessionManager sessionManager;
    
    public LoginEntity(Scene scene, UsuarioRepo usuarioRepo, SessionManager sessionManager) {
        super(scene);
        this.usuarioRepo = usuarioRepo;
        this.sessionManager = sessionManager;
        this.authService = new AuthService(usuarioRepo, sessionManager);
        
        txtUsername = new TextField(scene, 200, 35, 300, 220, "Username");
        txtPassword = new TextField(scene, 200, 35, 300, 280, "Password", true);

        btnLogin = new Button(scene, I18n.t("login"), 50, 42, 340, 350);
        btnBack = new Button(scene, I18n.t("back"), 50, 42, 340, 400);
        btnReactivar = new Button(scene, I18n.t("reactivate"), 20, 42, 340, 450);
        
        btnLogin.setListener(() -> intentarLogin());
        btnBack.setListener(() -> scene.cambiarAState(GameState.MENU_PRINCIPAL));
        btnReactivar.setListener(() -> scene.cambiarAState(GameState.REACTIVATE_ACCOUNT));
    }
    
    private void intentarLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        String resultado = authService.login(username, password);
        
        if (resultado.equals("Bienvenido")) {
            if (sessionManager.getUsuarioActual() != null) {
                I18n.setLanguage(sessionManager.getUsuarioActual().getIdioma());
            }
            scene.cambiarAState(GameState.MENU_SESION);
        } else {
            mensajeError = resultado;
            contadorError = 180;
        }
    }
    
    @Override
    protected void updateLogin()
    {
        if (!visible)
            return;

        txtUsername.update();
        txtPassword.update();

        btnLogin.update();
        btnBack.update();
        btnReactivar.update();

        if (contadorError > 0)
            contadorError--;
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        
        // Fondo
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);
        
        // Título
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(36f));
        g.drawString(I18n.t("login").toUpperCase(), 370, 150);
        
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
            int x = 400 - (g.getFontMetrics().stringWidth(mensajeError) / 2);
            g.drawString(mensajeError, x, 340);
        }
    }
    
    @Override
    public void gameStateChanged(GameState newGameState)
    {
        visible = (newGameState == GameState.LOGIN);

        if (visible)
        {
            txtUsername.clear();
            txtPassword.clear();

            mensajeError = "";
            contadorError = 0;

            btnLogin.reset();
            btnBack.reset();
            btnReactivar.reset();
            refreshText();
        }
    }

    private void refreshText() {
        btnLogin.setText(I18n.t("login"));
        btnBack.setText(I18n.t("back"));
        btnReactivar.setText(I18n.t("reactivate"));
    }
    
    @Override
    protected void updateLevelCleared() {}

    public TextField getTxtUsername() {
        return txtUsername;
    }

    public TextField getTxtPassword() {
        return txtPassword;
    }
    
}
