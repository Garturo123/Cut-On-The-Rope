package ctr.entity;

import Usuarios.Avatar;
import Usuarios.Usuario;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AvatarSelectorEntity extends Entity {
    
    private Button[] btnAvatares;
    private Button[] btnColores;
    private Button btnGuardar;
    private Button btnVolver;
    
    private ArrayList<String> avataresDisponibles;
    private ArrayList<String> coloresDisponibles;
    
    private int avatarSeleccionado = 0;
    private int colorSeleccionado = 0;
    private String avatarActual;
    private String colorActual;
    
    private Usuario usuarioActual;
    private Avatar avatarManager;
    private BufferedImage previewAvatar;
    
    public AvatarSelectorEntity(Scene scene, Usuario usuario) {
        super(scene);
        this.usuarioActual = usuario;
        this.avatarManager = new Avatar();
        
        avataresDisponibles = avatarManager.obtenerAvataresDisponibles();
        coloresDisponibles = avatarManager.obtenerColoresDisponibles();
        avatarActual = avatarManager.obtenerAvatarActual(usuario);
        colorActual = avatarManager.obtenerColorActual(usuario);
        
        avatarSeleccionado = avataresDisponibles.indexOf(avatarActual);
        if (avatarSeleccionado < 0) avatarSeleccionado = 0;
        colorSeleccionado = coloresDisponibles.indexOf(colorActual);
        if (colorSeleccionado < 0) colorSeleccionado = 0;
        
        // Botones de avatares (mostrar 12)
        btnAvatares = new Button[12];
        for (int i = 0; i < 12 && i < avataresDisponibles.size(); i++) {
            final int index = i;
            int col = i % 4;
            int row = i / 4;
            btnAvatares[i] = new Button(scene, "Selecionnar", 0, 0, 70 + col * 130, 120 + row * 100);
            btnAvatares[i].setListener(() -> {
                avatarSeleccionado = index;
                actualizarPreview();
            });
        }
        
        // Botones de colores
        btnColores = new Button[coloresDisponibles.size()];
        for (int i = 0; i < coloresDisponibles.size(); i++) {
            final int index = i;
            btnColores[i] = new Button(scene, "Color Avatar", 0, 0, 300 + i * 60, 420);
            btnColores[i].setListener(() -> {
                colorSeleccionado = index;
                actualizarPreview();
            });
        }
        
        btnGuardar = new Button(scene, "Save", 55, 28, 300, 520);
        btnVolver = new Button(scene, "Back", 50, 28, 400, 520);
        
        btnGuardar.setListener(() -> {
            avatarManager.guardar(usuarioActual, 
                                  avataresDisponibles.get(avatarSeleccionado), 
                                  coloresDisponibles.get(colorSeleccionado));
            scene.cambiarAState(GameState.PERFIL);
        });
        
        btnVolver.setListener(() -> scene.cambiarAState(GameState.PERFIL));
        
        actualizarPreview();
    }
    
    private void actualizarPreview() {
        String avatarPath = "/res/" + 
                           avataresDisponibles.get(avatarSeleccionado);
        previewAvatar = loadImageFromResource(avatarPath);
    }
    
    @Override
    public void update() {
        if (!visible) return;
        
        for (Button btn : btnAvatares) {
            if (btn != null) btn.update();
        }
        for (Button btn : btnColores) {
            if (btn != null) btn.update();
        }
        btnGuardar.update();
        btnVolver.update();
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 800, 600);
        
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(28f));
        g.drawString("SELECT AVATAR", 320, 70);
        
        // Dibujar avatares
        for (int i = 0; i < 12 && i < avataresDisponibles.size(); i++) {
            int col = i % 4;
            int row = i / 4;
            int x = 70 + col * 130;
            int y = 120 + row * 100;
            
            BufferedImage avatar = loadImageFromResource(
                "/res/" + avataresDisponibles.get(i));
            if (avatar != null) {
                g.drawImage(avatar, x + 5, y + 5, 50, 50, null);
            }
            
            if (avatarSeleccionado == i) {
                g.setColor(Color.YELLOW);
                g.drawRoundRect(x, y, 60, 60, 10, 10);
            }
            btnAvatares[i].draw(g);
        }
        
        // Selector de colores
        g.setFont(g.getFont().deriveFont(16f));
        g.drawString("Border Color:", 200, 415);
        
        for (int i = 0; i < coloresDisponibles.size(); i++) {
            int x = 300 + i * 60;
            g.setColor(Color.decode(coloresDisponibles.get(i)));
            g.fillRoundRect(x + 5, 425, 40, 40, 10, 10);
            
            if (colorSeleccionado == i) {
                g.setColor(Color.WHITE);
                g.drawRoundRect(x, 420, 50, 50, 12, 12);
            }
            btnColores[i].draw(g);
        }
        
        // Preview
        g.setFont(g.getFont().deriveFont(14f));
        g.drawString("Preview:", 550, 350);
        if (previewAvatar != null) {
            g.drawImage(previewAvatar, 560, 370, 80, 80, null);
            g.setColor(Color.decode(coloresDisponibles.get(colorSeleccionado)));
            g.drawRoundRect(558, 368, 84, 84, 12, 12);
        }
        
        btnGuardar.draw(g);
        btnVolver.draw(g);
    }
    
    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.AVATAR_SELECTOR);
        if (visible) {
            avatarActual = avatarManager.obtenerAvatarActual(usuarioActual);
            colorActual = avatarManager.obtenerColorActual(usuarioActual);
            avatarSeleccionado = avataresDisponibles.indexOf(avatarActual);
            colorSeleccionado = coloresDisponibles.indexOf(colorActual);
            if (avatarSeleccionado < 0) avatarSeleccionado = 0;
            if (colorSeleccionado < 0) colorSeleccionado = 0;
            actualizarPreview();
        }
    }
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}