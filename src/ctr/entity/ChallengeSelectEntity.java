package ctr.entity;

import Usuarios.Menu;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class ChallengeSelectEntity extends Entity {
    private final Menu menus;
    private final SessionManager sessionManager;
    private final Button btnPlay;
    private final Button btnVolver;

    public ChallengeSelectEntity(Scene scene, Menu menus, SessionManager sessionManager) {
        super(scene);
        this.menus = menus;
        this.sessionManager = sessionManager;
        btnPlay = new Button(scene, "Play", 60, 42, 315, 420);
        btnVolver = new Button(scene, "Back", 50, 42, 500, 420);
        btnPlay.setListener(() -> scene.cambiarAState(GameState.TITLE));
        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
    }

    @Override
    public void update() {
        if (!visible) return;
        btnPlay.update();
        btnVolver.update();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setColor(new Color(0, 0, 0, 210));
        g.fillRect(0, 0, 800, 600);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString("CHALLENGES", 315, 95);

        Usuario usuario = sessionManager.getUsuarioActual();
        List<String> retos = usuario != null ? usuario.getHistorialRetos() : java.util.Collections.emptyList();
        g.setFont(g.getFont().deriveFont(17f));
        if (retos.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.drawString("No challenges yet. Start a game to begin building your history.", 215, 250);
        } else {
            g.setColor(Color.WHITE);
            int y = 160;
            for (int i = Math.max(0, retos.size() - 8); i < retos.size(); i++) {
                g.drawString(retos.get(i), 180, y);
                y += 30;
            }
        }
        btnPlay.draw(g);
        btnVolver.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.CHALLENGE_SELECT);
        if (visible) {
            btnPlay.reset();
            btnVolver.reset();
        }
    }
}