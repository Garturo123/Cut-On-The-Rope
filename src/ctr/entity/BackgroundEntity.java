package ctr.entity;

import ctr.Entity;
import ctr.Scene;
import ctr.Scene.GameState;
import ctr.View;
import java.awt.Graphics2D;

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
        if (image != null) {
            g.drawImage(image, 0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT, null);
        }
    }
}
