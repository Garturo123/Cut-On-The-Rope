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

public class AmigosListEntity extends Entity {
    private final Menu menus;
    private final SessionManager sessionManager;
    private final Button btnVolver;

    public AmigosListEntity(Scene scene, Menu menus, SessionManager sessionManager) {
        super(scene);
        this.menus = menus;
        this.sessionManager = sessionManager;
        btnVolver = new Button(scene, "Back", 50, 28, 340, 520);
        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));
    }

    @Override
    public void update() {
        if (!visible) return;
        btnVolver.update();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setColor(new Color(0, 0, 0, 210));
        g.fillRect(0, 0, 800, 600);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString("FRIENDS", 345, 95);

        Usuario usuario = sessionManager.getUsuarioActual();
        List<String> amigos = usuario != null ? usuario.getAmigosRivales() : java.util.Collections.emptyList();
        g.setFont(g.getFont().deriveFont(18f));
        if (amigos.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.drawString("No friends added yet.", 320, 250);
        } else {
            g.setColor(Color.WHITE);
            int y = 165;
            for (String amigo : amigos) {
                g.drawString("- " + amigo, 310, y);
                y += 32;
            }
        }
        btnVolver.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.AMIGOS_LIST);
        if (visible) btnVolver.reset();
    }
}