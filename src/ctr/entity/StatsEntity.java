package ctr.entity;

import Usuarios.Menu;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import ctr.Entity;
import ctr.I18n;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;

public class StatsEntity extends Entity {
    private final Menu menus;
    private final SessionManager sessionManager;
    private final Button btnVolver;

    public StatsEntity(Scene scene, Menu menus, SessionManager sessionManager) {
        super(scene);
        this.menus = menus;
        this.sessionManager = sessionManager;
        btnVolver = new Button(scene, I18n.t("back"), 50, 42, 415, 610);
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
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString(I18n.t("stats_title"), 405, 95);

        Usuario usuario = sessionManager.getUsuarioActual();
        g.setFont(g.getFont().deriveFont(20f));
        int y = 180;
        if (usuario == null) {
            g.setColor(Color.YELLOW);
            g.drawString(I18n.t("no_session"), 335, y);
        } else {
            g.drawString(I18n.t("total_score") + usuario.getPuntuacionGeneral(), 310, y);
            g.drawString(I18n.t("levels_completed") + usuario.getNivelesCompletados(), 310, y + 45);
            g.drawString(I18n.t("challenges_won") + usuario.getRetosGanados(), 310, y + 90);
            g.drawString(I18n.t("unlocked_level") + usuario.getNivelDesbloqueado(), 310, y + 135);
            g.drawString(I18n.t("friends") + ": " + usuario.getAmigosRivales().size(), 310, y + 180);
        }
        btnVolver.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.STATS);
        if (visible) {
            btnVolver.setText(I18n.t("back"));
            btnVolver.reset();
        }
    }
}
