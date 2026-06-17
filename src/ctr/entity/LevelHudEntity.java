package ctr.entity;

import Audio.Manager;
import ctr.Entity;
import ctr.I18n;
import ctr.Mouse;
import ctr.ResourceLoader;
import ctr.Scene;
import ctr.View;
import ctr.model.Star;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LevelHudEntity extends Entity {
    private static final Color INK = new Color(46, 35, 26);
    private static final Color PAPER = new Color(238, 224, 205, 230);
    private static final Color STAR_ON = new Color(255, 230, 64);
    private static final Color STAR_GLOW = new Color(255, 224, 48, 80);

    private final Manager audioManager;
    private final BufferedImage playIcon;
    private final BufferedImage levelsIcon;
    private final BufferedImage pauseIcon;
    private final BufferedImage replayIcon;
    private final BufferedImage muteIcon;
    private final BufferedImage soundIcon;

    public LevelHudEntity(Scene scene, Manager audioManager) {
        super(scene);
        this.audioManager = audioManager;
        playIcon = loadImageFromResource("/res/play.png");
        levelsIcon = loadImageFromResource("/res/selectLevel.png");
        pauseIcon = loadOptionalImage("/res/pause.png");
        replayIcon = loadOptionalImage("/res/re_play.png");
        muteIcon = loadOptionalImage("/res/mute.png");
        soundIcon = loadOptionalImage("/res/sound.png");
    }

    @Override
    public void update() {
        if (!visible) return;

        if (scene.isLevelPaused()) {
            updatePauseMenu();
            return;
        }

        if (consumeClick(restartButton())) {
            scene.replayLevel();
        } else if (consumeClick(pauseButton())) {
            scene.setLevelPaused(true);
        }
    }

    private void updatePauseMenu() {
        if (consumeClick(resumeButton())) {
            scene.setLevelPaused(false);
        } else if (consumeClick(restartMenuButton())) {
            scene.replayLevel();
        } else if (consumeClick(levelsButton())) {
            scene.exitCurrentLevel();
        } else if (consumeClick(muteButton())) {
            audioManager.alternarMuteGeneral();
        }
    }

    private boolean consumeClick(Rectangle rect) {
        if (!Mouse.pressed || Mouse.pressedConsumed || !rect.contains(Mouse.x, Mouse.y))
            return false;
        Mouse.pressedConsumed = true;
        scene.reproducirSFX("SoundEfects/tap.wav");
        return true;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawStarMeter(g);
        drawIconButton(g, restartButton(), "restart", false);
        drawIconButton(g, pauseButton(), "pause", false);
        drawLevelLabel(g);
        drawChallengeTimer(g);
        drawTutorialHint(g);

        if (scene.isLevelPaused()) {
            drawPausePopup(g);
        }
    }

    private void drawStarMeter(Graphics2D g) {
        int collected = countObtainedStars();
        int spacing = 64;
        int startX = View.SCREEN_WIDTH / 2 - spacing * (Scene.MAX_STARS - 1) / 2;
        for (int i = 0; i < Scene.MAX_STARS; i++) {
            int cx = startX + i * spacing;
            Shape glow = createStar(cx, 70, 30, 13);
            if (i < collected) {
                g.setColor(STAR_GLOW);
                g.fillOval(cx - 30, 40, 60, 60);
                g.setColor(STAR_ON);
                g.fill(glow);
            }
            g.setColor(new Color(255, 246, 210, 160));
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(glow);
            g.setColor(new Color(80, 63, 44, 180));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(glow);
        }
    }

    private int countObtainedStars() {
        int count = 0;
        for (Star star : scene.getModel().getStars()) {
            if (!star.isVisible())
                count++;
        }
        return count;
    }

    private void drawLevelLabel(Graphics2D g) {
        Font font = new Font("Comic Sans MS", Font.BOLD, 34);
        g.setFont(font);
        drawOutlinedText(g, I18n.t("level"), 18, View.SCREEN_HEIGHT - 92, 2.5f);
        drawOutlinedText(g, "1 - " + scene.getCurrentLevel(), 18, View.SCREEN_HEIGHT - 40, 3.5f);
    }

    private void drawChallengeTimer(Graphics2D g) {
        if (!scene.isFriendlyChallengeActive()) return;

        int seconds = scene.getChallengeRemainingSeconds();
        String time = String.format("%02d:%02d", seconds / 60, seconds % 60);
        String text = I18n.t("challenge") + " " + time;

        g.setFont(new Font("Comic Sans MS", Font.BOLD, 27));
        int x = View.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(text) / 2;
        drawOutlinedText(g, text, x, 125, 2.5f);

        g.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        String progress = scene.getChallengeCompletedLevels() + "/" + Scene.MAX_LEVEL + " | " + scene.getChallengeStars() + " " + I18n.t("stars");
        x = View.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(progress) / 2;
        drawInkText(g, progress, x, 152, 1.5f);
    }

    private void drawTutorialHint(Graphics2D g) {
        String text = I18n.t("tutorial_" + scene.getCurrentLevel());
        if (text.startsWith("tutorial_"))
            return;

        Rectangle area = hintArea(scene.getCurrentLevel());
        drawLightBulb(g, area.x - 65, area.y + 10);

        Font font = new Font("Comic Sans MS", Font.BOLD, 33);
        g.setFont(font);
        List<String> lines = wrapText(g, text, area.width);
        int y = area.y;
        for (String line : lines) {
            int x = area.x + (area.width - g.getFontMetrics().stringWidth(line)) / 2;
            drawInkText(g, line, x, y, 2.5f);
            y += 42;
        }
    }

    private Rectangle hintArea(int level) {
        switch (level) {
            case 1: return new Rectangle(560, 260, 350, 120);
            case 2: return new Rectangle(560, 315, 350, 120);
            case 3: return new Rectangle(610, 330, 330, 120);
            case 4: return new Rectangle(520, 260, 350, 120);
            case 5: return new Rectangle(600, 310, 330, 120);
            case 6: return new Rectangle(80, 245, 390, 150);
            case 7: return new Rectangle(700, 350, 270, 120);
            case 10: return new Rectangle(110, 260, 300, 120);
            default: return new Rectangle(300, 360, 430, 120);
        }
    }

    private void drawPausePopup(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);

        int x = View.SCREEN_WIDTH / 2 - 210;
        int y = 140;
        g.setColor(new Color(58, 42, 28, 230));
        g.fillRoundRect(x, y, 420, 420, 24, 24);
        g.setColor(new Color(255, 237, 201, 230));
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(x + 6, y + 6, 408, 408, 20, 20);

        g.setFont(new Font("Comic Sans MS", Font.BOLD, 42));
        String title = I18n.t("pause");
        drawOutlinedText(g, title, View.SCREEN_WIDTH / 2 - g.getFontMetrics().stringWidth(title) / 2, y + 72, 3f);

        drawPauseMenuButton(g, resumeButton(), "resume", I18n.t("resume"));
        drawPauseMenuButton(g, restartMenuButton(), "restart", I18n.t("restart"));
        drawPauseMenuButton(g, levelsButton(), "levels", I18n.t("level_selector"));
        drawPauseMenuButton(g, muteButton(), "mute", audioManager.isMute() ? I18n.t("unmute") : I18n.t("mute"));
    }

    private void drawPauseMenuButton(Graphics2D g, Rectangle rect, String icon, String label) {
        drawIconButton(g, rect, icon, true);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(label, rect.x + (rect.width - metrics.stringWidth(label)) / 2, rect.y + rect.height + 24);
    }

    private void drawIconButton(Graphics2D g, Rectangle rect, String icon, boolean circle) {
        boolean over = rect.contains(Mouse.x, Mouse.y);
        g.setColor(over ? new Color(255, 246, 224, 245) : PAPER);
        if (circle) {
            g.fillOval(rect.x, rect.y, rect.width, rect.height);
        } else {
            g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 24, 24);
        }
        g.setColor(new Color(79, 63, 47));
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (circle) {
            g.drawOval(rect.x, rect.y, rect.width, rect.height);
        } else {
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 24, 24);
        }

        if ("resume".equals(icon)) {
            drawResourceIcon(g, rect, playIcon, circle);
        } else if ("levels".equals(icon)) {
            drawResourceIcon(g, rect, levelsIcon, circle);
        } else if ("pause".equals(icon)) {
            if (pauseIcon != null)
                drawResourceIcon(g, rect, pauseIcon, circle);
            else
                drawPauseIcon(g, rect);
        } else if ("restart".equals(icon)) {
            if (replayIcon != null)
                drawResourceIcon(g, rect, replayIcon, circle);
            else
                drawRestartIcon(g, rect);
        } else if ("mute".equals(icon)) {
            BufferedImage audioIcon = audioManager.isMute() ? muteIcon : soundIcon;
            if (audioIcon != null)
                drawResourceIcon(g, rect, audioIcon, circle);
            else
                drawMuteIcon(g, rect);
        }
    }

    private BufferedImage loadOptionalImage(String resource) {
        try {
            return ResourceLoader.loadImage(resource);
        } catch (Exception ex) {
            return null;
        }
    }

    private void drawResourceIcon(Graphics2D g, Rectangle rect, BufferedImage source, boolean circle) {
        Shape oldClip = g.getClip();
        Shape clip = circle
            ? new java.awt.geom.Ellipse2D.Double(rect.x + 6, rect.y + 6, rect.width - 12, rect.height - 12)
            : new java.awt.geom.RoundRectangle2D.Double(rect.x + 6, rect.y + 6, rect.width - 12, rect.height - 12, 18, 18);
        g.setClip(clip);

        int side = Math.min(source.getWidth(), source.getHeight());
        int sx = (source.getWidth() - side) / 2;
        int sy = (source.getHeight() - side) / 2;
        g.drawImage(
            source,
            rect.x + 6,
            rect.y + 6,
            rect.x + rect.width - 6,
            rect.y + rect.height - 6,
            sx,
            sy,
            sx + side,
            sy + side,
            null
        );
        g.setClip(oldClip);
    }

    private void drawPauseIcon(Graphics2D g, Rectangle rect) {
        g.setColor(INK);
        int barW = rect.width / 7;
        int h = rect.height / 2;
        int y = rect.y + (rect.height - h) / 2;
        g.fillRoundRect(rect.x + rect.width / 3 - barW, y, barW, h, 6, 6);
        g.fillRoundRect(rect.x + rect.width * 2 / 3, y, barW, h, 6, 6);
    }

    private void drawRestartIcon(Graphics2D g, Rectangle rect) {
        int cx = rect.x + rect.width / 2;
        int cy = rect.y + rect.height / 2;
        int r = Math.min(rect.width, rect.height) / 4;
        g.setColor(INK);
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(cx - r, cy - r, r * 2, r * 2, 40, 285);
        Path2D arrow = new Path2D.Double();
        arrow.moveTo(cx - r - 7, cy - 2);
        arrow.lineTo(cx - r - 2, cy - 22);
        arrow.lineTo(cx + 11, cy - 8);
        arrow.closePath();
        g.fill(arrow);
    }

    private void drawMuteIcon(Graphics2D g, Rectangle rect) {
        int x = rect.x + rect.width / 3;
        int y = rect.y + rect.height / 2;
        Path2D speaker = new Path2D.Double();
        speaker.moveTo(x - 18, y - 10);
        speaker.lineTo(x - 6, y - 10);
        speaker.lineTo(x + 10, y - 24);
        speaker.lineTo(x + 10, y + 24);
        speaker.lineTo(x - 6, y + 10);
        speaker.lineTo(x - 18, y + 10);
        speaker.closePath();
        g.setColor(INK);
        g.fill(speaker);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (audioManager.isMute()) {
            g.drawLine(rect.x + rect.width - 25, rect.y + 22, rect.x + rect.width - 52, rect.y + rect.height - 22);
            g.drawLine(rect.x + rect.width - 52, rect.y + 22, rect.x + rect.width - 25, rect.y + rect.height - 22);
        } else {
            g.drawArc(x + 8, y - 20, 34, 40, -40, 80);
            g.drawArc(x + 16, y - 30, 48, 60, -35, 70);
        }
    }

    private void drawLightBulb(Graphics2D g, int x, int y) {
        Stroke original = g.getStroke();
        g.setColor(new Color(40, 32, 24, 210));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(x, y, 52, 64);
        g.drawLine(x + 18, y + 65, x + 36, y + 65);
        g.drawLine(x + 20, y + 75, x + 34, y + 75);
        g.drawLine(x + 24, y + 85, x + 31, y + 85);
        g.drawLine(x + 25, y + 46, x + 18, y + 31);
        g.drawLine(x + 25, y + 46, x + 33, y + 31);
        g.drawLine(x + 25, y + 46, x + 25, y + 25);
        g.setStroke(original);
    }

    private List<String> wrapText(Graphics2D g, String text, int maxWidth) {
        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.split(" ");
        String line = "";
        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (g.getFontMetrics().stringWidth(candidate) <= maxWidth) {
                line = candidate;
            } else {
                if (!line.isEmpty()) lines.add(line);
                line = word;
            }
        }
        if (!line.isEmpty()) lines.add(line);
        return lines;
    }

    private void drawOutlinedText(Graphics2D g, String text, int x, int y, float strokeWidth) {
        TextLayout layout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
        Shape outline = layout.getOutline(AffineTransform.getTranslateInstance(x, y));
        Stroke original = g.getStroke();
        g.setColor(new Color(255, 255, 255, 205));
        g.setStroke(new BasicStroke(strokeWidth * 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(outline);
        g.setColor(INK);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(outline);
        g.setColor(Color.WHITE);
        g.fill(outline);
        g.setStroke(original);
    }

    private void drawInkText(Graphics2D g, String text, int x, int y, float strokeWidth) {
        TextLayout layout = new TextLayout(text, g.getFont(), g.getFontRenderContext());
        Shape outline = layout.getOutline(AffineTransform.getTranslateInstance(x, y));
        Stroke original = g.getStroke();
        g.setColor(new Color(255, 235, 175, 95));
        g.setStroke(new BasicStroke(strokeWidth * 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(outline);
        g.setColor(INK);
        g.fill(outline);
        g.setStroke(original);
    }

    private Shape createStar(double cx, double cy, double outerRadius, double innerRadius) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2.0 + i * Math.PI / 5.0;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = cx + Math.cos(angle) * radius;
            double y = cy + Math.sin(angle) * radius;
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }

    private Rectangle restartButton() {
        return new Rectangle(View.SCREEN_WIDTH - 154, 38, 60, 60);
    }

    private Rectangle pauseButton() {
        return new Rectangle(View.SCREEN_WIDTH - 82, 38, 64, 60);
    }

    private Rectangle resumeButton() {
        return new Rectangle(View.SCREEN_WIDTH / 2 - 142, 285, 92, 92);
    }

    private Rectangle restartMenuButton() {
        return new Rectangle(View.SCREEN_WIDTH / 2 + 50, 285, 92, 92);
    }

    private Rectangle levelsButton() {
        return new Rectangle(View.SCREEN_WIDTH / 2 - 142, 425, 92, 92);
    }

    private Rectangle muteButton() {
        return new Rectangle(View.SCREEN_WIDTH / 2 + 50, 425, 92, 92);
    }

    @Override
    public void gameStateChanged(Scene.GameState newGameState) {
        visible = newGameState == Scene.GameState.PLAYING;
    }
}
