package ctr.ui;

import ctr.Entity;
import ctr.FontRenderer;
import ctr.Mouse;
import ctr.Scene;
import ctr.physics.Vec2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class TextField extends Entity {
    
    private String text = "";
    private String placeholder;
    private boolean focused;
    private final Vec2 position = new Vec2();
    private final Rectangle rectangle = new Rectangle();
    private int width, height;
    private boolean password;
    private int maxLength;
    private int cursorBlink;
    private boolean showCursor;
    
    // Para almacenar teclas
    private static StringBuilder teclasBuffer = new StringBuilder();
    
    public TextField(Scene scene, int width, int height, int x, int y, String placeholder) {
        this(scene, width, height, x, y, placeholder, false, 50);
    }
    
    public TextField(Scene scene, int width, int height, int x, int y, String placeholder, boolean password) {
        this(scene, width, height, x, y, placeholder, password, 50);
    }
    
    public TextField(Scene scene, int width, int height, int x, int y, String placeholder, boolean password, int maxLength) {
        super(scene);
        this.width = width;
        this.height = height;
        this.position.set(x, y);
        this.rectangle.setBounds(x, y, width, height);
        this.placeholder = placeholder;
        this.password = password;
        this.maxLength = maxLength;
        this.focused = false;
        this.visible = true;
        this.cursorBlink = 0;
        this.showCursor = false;
    }
    
    public String getText() { return text; }
    
    public void setText(String nuevoTexto) {
        if (nuevoTexto != null && nuevoTexto.length() <= maxLength) {
            text = nuevoTexto;
        }
    }
    
    public void clear() { text = ""; }
    
    public void agregarCaracter(char c) {
        if (!focused) return;
        if (text.length() < maxLength && c >= 32 && c <= 126) {
            text += c;
        }
    }
    
    public void borrarCaracter() {
        if (!focused) return;
        if (text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
    }
    
    public void limpiarCompleto() {
        if (!focused) return;
        text = "";
    }
    
    @Override
    public void update() {
        if (!visible) {
            focused = false;
            return;
        }
        
        // Click para focus
        boolean over = rectangle.contains(Mouse.x, Mouse.y);
        if (over && !Mouse.pressedConsumed && Mouse.pressed) {
            Mouse.pressedConsumed = true;
            focused = true;
        }
        
        // Perder focus al hacer click fuera
        if (!over && Mouse.pressed && !Mouse.pressedConsumed && focused) {
            focused = false;
        }
        
        // Animación cursor
        cursorBlink++;
        if (cursorBlink > 30) {
            cursorBlink = 0;
            showCursor = !showCursor;
        }
    }
    
    public void procesarTecla(int keyCode, char keyChar) {
        if (!focused) return;
        
        if (keyCode == 10) { // Enter
            focused = false;
        } else if (keyCode == 8) { // Backspace
            borrarCaracter();
        } else if (keyCode == 27) { // Escape
            clear();
            focused = false;
        } else if (keyChar >= 32 && keyChar <= 126) {
            agregarCaracter(keyChar);
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo
        if (focused) {
            g.setColor(new Color(40, 40, 60));
        } else {
            g.setColor(new Color(30, 30, 40));
        }
        g.fillRoundRect((int) position.x, (int) position.y, width, height, 10, 10);
        
        // Borde
        if (focused) {
            g.setColor(new Color(255, 200, 100));
        } else {
            g.setColor(new Color(100, 150, 200));
        }
        g.drawRoundRect((int) position.x, (int) position.y, width, height, 10, 10);
        
        // Texto
        g.setFont(g.getFont().deriveFont(16f));
        
        String textoAMostrar;
        if (text.isEmpty() && !focused) {
            textoAMostrar = placeholder;
            g.setColor(new Color(150, 150, 180));
        } else {
            if (password && !text.isEmpty()) {
                textoAMostrar = "•".repeat(text.length());
            } else {
                textoAMostrar = text;
            }
            g.setColor(Color.WHITE);
        }
        
        int textX = (int) position.x + 10;
        int textY = (int) position.y + height / 2 + 5;
        FontRenderer.draw(g, textoAMostrar, textX, textY);
        
        // Cursor
        if (focused && showCursor) {
            int cursorX = textX + (textoAMostrar.length() * 8);
            g.setColor(Color.WHITE);
            g.drawLine(cursorX, textY - 12, cursorX, textY + 4);
        }
    }
    
    @Override
    public void gameStateChanged(Scene.GameState newGameState) {}
    
    @Override
    protected void updateLevelCleared() {}
    
    @Override
    protected void updateFixedLevelCleared() {}
}