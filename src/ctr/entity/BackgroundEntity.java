package ctr.entity;

import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class BackgroundEntity extends Entity 
{
    public BackgroundEntity(Scene scene)    {   super(scene);   }

    @Override
    public void start() {   loadImageFromResource("/res/background.png");   }

    @Override
    public void gameStateChanged(GameState newGameState)   
    {
        visible = newGameState == GameState.PLAYING
               || newGameState == GameState.LEVEL_CLEARED
               || newGameState == GameState.GAME_OVER;
    }

    @Override
    public void draw(Graphics2D g) 
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint base = new GradientPaint(0, 0, new Color(206, 151, 87),
            View.SCREEN_WIDTH, View.SCREEN_HEIGHT, new Color(121, 74, 38));
        g.setPaint(base);
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);

        g.setColor(new Color(243, 183, 101, 210));
        g.fillRoundRect(38, 18, View.SCREEN_WIDTH - 76, View.SCREEN_HEIGHT - 42, 28, 28);

        g.setColor(new Color(119, 78, 45, 80));
        g.fillRect(0, 0, 40, View.SCREEN_HEIGHT);
        g.fillRect(View.SCREEN_WIDTH - 40, 0, 40, View.SCREEN_HEIGHT);
        g.drawLine(40, 120, 40, View.SCREEN_HEIGHT - 125);
        g.drawLine(View.SCREEN_WIDTH - 40, 120, View.SCREEN_WIDTH - 40, View.SCREEN_HEIGHT - 125);

        drawPatch(g, 130, 95, 220, 165, -5, new Color(164, 112, 57, 90));
        drawPatch(g, 405, 75, 250, 205, 3, new Color(247, 190, 111, 130));
        drawPatch(g, 735, 290, 165, 190, 8, new Color(139, 91, 48, 95));
        drawPatch(g, 90, 420, 310, 240, -4, new Color(155, 101, 49, 105));
        drawPatch(g, 390, 345, 230, 220, 2, new Color(247, 186, 102, 110));

        g.setColor(new Color(255, 222, 142, 42));
        g.fillOval(185, 230, 420, 760);
    }

    private void drawPatch(Graphics2D g, int x, int y, int w, int h, double angle, Color color) {
        java.awt.geom.AffineTransform original = g.getTransform();
        g.rotate(Math.toRadians(angle), x + w / 2.0, y + h / 2.0);
        g.setColor(color);
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 20, 20));
        g.setColor(new Color(80, 45, 24, 28));
        for (int i = 0; i < h; i += 18) {
            g.drawLine(x + 8, y + i, x + w - 8, y + i + 8);
        }
        Path2D shadow = new Path2D.Double();
        shadow.moveTo(x, y + h);
        shadow.lineTo(x + w, y + h - 18);
        shadow.lineTo(x + w, y + h);
        shadow.closePath();
        g.setColor(new Color(80, 45, 24, 35));
        g.fill(shadow);
        g.setTransform(original);
    }
}
