package ctr.entity;

import Usuarios.Niveles;
import Usuarios.PuzzleService;
import Usuarios.SessionManager;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class LevelSelectEntity extends Entity {
    private static final int MAX_LEVEL = 5;

    private final PuzzleService puzzleService;
    private final SessionManager sessionManager;
    private final Button[] levelButtons = new Button[MAX_LEVEL];
    private final Button btnBack;
    private ArrayList<Niveles> niveles = new ArrayList<>();
    private String mensaje = "";
    private int contadorMensaje;

    public LevelSelectEntity(Scene scene, PuzzleService puzzleService, SessionManager sessionManager) {
        super(scene);
        this.puzzleService = puzzleService;
        this.sessionManager = sessionManager;

        for (int i = 0; i < MAX_LEVEL; i++) {
            final int level = i + 1;
            int col = i % 3;
            int row = i / 3;
            levelButtons[i] = new Button(scene, "Level " + level, 30, 42, 125 + col * 185, 180 + row * 130);
            levelButtons[i].setListener(() -> seleccionarNivel(level));
        }

        btnBack = new Button(scene, "Back", 50, 42, 315, 500);
        btnBack.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
    }

    private void seleccionarNivel(int level) {
        if (scene.canPlayLevel(level)) {
            scene.startLevel(level);
            return;
        }

        mensaje = "Complete the previous level first";
        contadorMensaje = 120;
    }

    @Override
    protected void updateLevelSelect() {
        if (!visible) return;

        for (Button button : levelButtons) {
            button.update();
        }
        btnBack.update();

        if (contadorMensaje > 0) contadorMensaje--;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, 800, 600);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(32f));
        g.drawString("SELECT LEVEL", 300, 95);

        g.setFont(g.getFont().deriveFont(15f));
        for (int i = 0; i < MAX_LEVEL; i++) {
            int level = i + 1;
            int col = i % 3;
            int row = i / 3;
            int x = 125 + col * 185;
            int y = 180 + row * 130;
            boolean unlocked = scene.canPlayLevel(level);
            Niveles nivel = buscarNivel(level);
            int stars = nivel != null ? nivel.getMejoresEstrellas() : 0;

            if (!unlocked) {
                g.setColor(new Color(80, 80, 80, 170));
                g.fillRoundRect(x - 5, y - 8, 176, 96, 12, 12);
            }

            levelButtons[i].draw(g);
            g.setColor(unlocked ? Color.WHITE : Color.LIGHT_GRAY);
            g.drawString(unlocked ? "Unlocked" : "Locked", x + 45, y + 78);
            g.setColor(new Color(255, 215, 80));
            g.drawString(formatStars(stars), x + 52, y + 102);
        }

        btnBack.draw(g);

        if (contadorMensaje > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(16f));
            int x = 400 - g.getFontMetrics().stringWidth(mensaje) / 2;
            g.drawString(mensaje, x, 470);
        }
    }

    private Niveles buscarNivel(int level) {
        for (Niveles nivel : niveles) {
            if (nivel.getNivel() == level) return nivel;
        }
        return null;
    }

    private String formatStars(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i < stars ? '*' : '-');
        }
        return sb.toString();
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.LEVEL_SELECT);
        if (visible) {
            niveles = puzzleService.obtenerPuzzles();
            contadorMensaje = 0;
            for (Button button : levelButtons) {
                button.reset();
            }
            btnBack.reset();
        }
    }
}