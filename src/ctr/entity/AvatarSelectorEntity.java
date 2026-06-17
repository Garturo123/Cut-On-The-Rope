package ctr.entity;

import Usuarios.Avatar;
import Usuarios.SessionManager;
import Usuarios.Usuario;
import ctr.Entity;
import ctr.I18n;
import ctr.Mouse;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import ctr.ui.Button;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AvatarSelectorEntity extends Entity {

    private static final int AVATAR_COLUMNS = 7;
    private static final int AVATAR_TILE_SIZE = 70;
    private static final int AVATAR_TILE_GAP = 25;
    private static final int AVATAR_START_X = 55;
    private static final int AVATAR_START_Y = 120;
    private static final int COLOR_SIZE = 48;
    private static final int COLOR_GAP = 22;
    private static final int COLOR_START_X = 90;
    private static final int COLOR_START_Y = 500;

    private Button btnGuardar;
    private Button btnVolver;

    private ArrayList<String> avataresDisponibles;
    private ArrayList<String> coloresDisponibles;
    private BufferedImage[] avatarImages;

    private int avatarSeleccionado = 0;
    private int colorSeleccionado = 0;
    private String avatarActual;
    private String colorActual;

    private final SessionManager sessionManager;
    private Usuario usuarioActual;
    private Avatar avatarManager;
    private BufferedImage previewAvatar;

    public AvatarSelectorEntity(Scene scene, SessionManager sessionManager) {
        super(scene);
        this.sessionManager = sessionManager;
        this.usuarioActual = sessionManager.getUsuarioActual();
        this.avatarManager = new Avatar();

        avataresDisponibles = avatarManager.obtenerAvataresDisponibles();
        coloresDisponibles = avatarManager.obtenerColoresDisponibles();
        avatarActual = avatarManager.obtenerAvatarActual(usuarioActual);
        colorActual = avatarManager.obtenerColorActual(usuarioActual);

        avatarSeleccionado = avataresDisponibles.indexOf(avatarActual);
        if (avatarSeleccionado < 0) avatarSeleccionado = 0;
        colorSeleccionado = coloresDisponibles.indexOf(colorActual);
        if (colorSeleccionado < 0) colorSeleccionado = 0;

        avatarImages = new BufferedImage[avataresDisponibles.size()];
        for (int i = 0; i < avataresDisponibles.size(); i++) {
            avatarImages[i] = loadImageFromResource("/res/" + avataresDisponibles.get(i));
        }

        btnGuardar = new Button(scene, I18n.t("save"), 55, 28, 320, 610);
        btnVolver = new Button(scene, I18n.t("back"), 50, 28, 510, 610);

        btnGuardar.setListener(() -> {
            usuarioActual = sessionManager.getUsuarioActual();
            if (usuarioActual != null) {
                avatarManager.guardar(usuarioActual,
                                      avataresDisponibles.get(avatarSeleccionado),
                                      coloresDisponibles.get(colorSeleccionado));
            }
            scene.cambiarAState(GameState.MENU_SESION);
        });

        btnVolver.setListener(() -> scene.cambiarAState(GameState.MENU_SESION));

        actualizarPreview();
    }

    private void actualizarPreview() {
        String avatarPath = "/res/" + avataresDisponibles.get(avatarSeleccionado);
        previewAvatar = loadImageFromResource(avatarPath);
    }

    @Override
    public void update() {
        if (!visible) return;

        if (Mouse.pressed && !Mouse.pressedConsumed) {
            int avatarIndex = avatarIndexAtMouse();
            if (avatarIndex >= 0) {
                avatarSeleccionado = avatarIndex;
                actualizarPreview();
                Mouse.pressedConsumed = true;
            }

            int colorIndex = colorIndexAtMouse();
            if (colorIndex >= 0) {
                colorSeleccionado = colorIndex;
                actualizarPreview();
                Mouse.pressedConsumed = true;
            }
        }
        btnGuardar.update();
        btnVolver.update();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(28f));
        g.drawString(I18n.t("select_avatar"), 370, 70);

        for (int i = 0; i < avataresDisponibles.size(); i++) {
            int x = avatarX(i);
            int y = avatarY(i);

            g.setColor(new Color(255, 255, 255, 35));
            g.fillRoundRect(x, y, AVATAR_TILE_SIZE, AVATAR_TILE_SIZE, 12, 12);

            if (avatarImages[i] != null) {
                g.drawImage(avatarImages[i], x + 8, y + 8, 54, 54, null);
            }

            if (avatarSeleccionado == i) {
                g.setColor(Color.YELLOW);
                g.drawRoundRect(x - 3, y - 3, AVATAR_TILE_SIZE + 6, AVATAR_TILE_SIZE + 6, 14, 14);
            }
        }

        g.setFont(g.getFont().deriveFont(16f));
        g.setColor(Color.WHITE);
        g.drawString(I18n.t("border_color"), 90, 485);

        for (int i = 0; i < coloresDisponibles.size(); i++) {
            int x = colorX(i);
            g.setColor(Color.decode(coloresDisponibles.get(i)));
            g.fillRoundRect(x, COLOR_START_Y, COLOR_SIZE, COLOR_SIZE, 10, 10);

            if (colorSeleccionado == i) {
                g.setColor(Color.WHITE);
                g.drawRoundRect(x - 4, COLOR_START_Y - 4, COLOR_SIZE + 8, COLOR_SIZE + 8, 12, 12);
            }
        }

        g.setFont(g.getFont().deriveFont(14f));
        g.setColor(Color.WHITE);
        g.drawString(I18n.t("preview"), 790, 170);
        if (previewAvatar != null) {
            g.drawImage(previewAvatar, 785, 190, 110, 110, null);
            g.setColor(Color.decode(coloresDisponibles.get(colorSeleccionado)));
            g.drawRoundRect(781, 186, 118, 118, 14, 14);
        }

        btnGuardar.draw(g);
        btnVolver.draw(g);
    }

    private int avatarIndexAtMouse() {
        for (int i = 0; i < avataresDisponibles.size(); i++) {
            if (isMouseInside(avatarX(i), avatarY(i), AVATAR_TILE_SIZE, AVATAR_TILE_SIZE)) {
                return i;
            }
        }
        return -1;
    }

    private int colorIndexAtMouse() {
        for (int i = 0; i < coloresDisponibles.size(); i++) {
            if (isMouseInside(colorX(i), COLOR_START_Y, COLOR_SIZE, COLOR_SIZE)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isMouseInside(int x, int y, int width, int height) {
        return Mouse.x >= x && Mouse.x <= x + width && Mouse.y >= y && Mouse.y <= y + height;
    }

    private int avatarX(int index) {
        int col = index % AVATAR_COLUMNS;
        return AVATAR_START_X + col * (AVATAR_TILE_SIZE + AVATAR_TILE_GAP);
    }

    private int avatarY(int index) {
        int row = index / AVATAR_COLUMNS;
        return AVATAR_START_Y + row * (AVATAR_TILE_SIZE + AVATAR_TILE_GAP);
    }

    private int colorX(int index) {
        return COLOR_START_X + index * (COLOR_SIZE + COLOR_GAP);
    }

    @Override
    public void gameStateChanged(GameState newGameState) {
        visible = (newGameState == GameState.AVATAR_SELECTOR);
        if (visible) {
            usuarioActual = sessionManager.getUsuarioActual();
            avatarActual = avatarManager.obtenerAvatarActual(usuarioActual);
            colorActual = avatarManager.obtenerColorActual(usuarioActual);
            avatarSeleccionado = avataresDisponibles.indexOf(avatarActual);
            colorSeleccionado = coloresDisponibles.indexOf(colorActual);
            if (avatarSeleccionado < 0) avatarSeleccionado = 0;
            if (colorSeleccionado < 0) colorSeleccionado = 0;
            btnGuardar.reset();
            btnVolver.reset();
            refreshText();
            actualizarPreview();
        }
    }

    private void refreshText() {
        btnGuardar.setText(I18n.t("save"));
        btnVolver.setText(I18n.t("back"));
    }

    @Override
    protected void updateLevelCleared() {}
}
