package ctr;
import ctr.Scene.GameState;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Entity 
{
    protected boolean visible = false;
    protected Scene scene;
    protected BufferedImage image;
    protected int instructionPointer;
    protected long waitTime;
    
    public Entity(Scene scene)  {    this.scene = scene;    }

    public boolean isVisible()  {    return visible; }

    public void setVisible(boolean visible) {   this.visible = visible; }

    protected BufferedImage loadImageFromResource(String resource) 
    {
        try 
        {
            image = ImageIO.read(getClass().getResourceAsStream(resource));
        } catch (IOException ex) 
        {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return image;
    }
    
    public void start() {   }
    
    public void update() 
    {
        switch (scene.getGameState()) 
        {
            case INITIALIZING: updateInitializing(); break;
            case TITLE: updateTitle(); break;
            case LEVEL_SELECT: updateLevelSelect(); break;
            case READY: updateReady(); break;
            case PLAYING: updatePlaying(); break;
            case LEVEL_CLEARED: updateLevelCleared(); break;
            case GAME_OVER: updateGameOver(); break;
        }
    }
    
    public void updateFixed() 
    {
        switch (scene.getGameState()) 
        {
            case INITIALIZING: updateFixedInitializing(); break;
            case OL_PRESENTS: updateFixedOLPresents(); break;
            case TITLE: updateFixedTitle(); break;
            case LEVEL_SELECT: updateFixedLevelSelect(); break;
            case READY: updateFixedReady(); break;
            case PLAYING: updateFixedPlaying(); break;
            case LEVEL_CLEARED: updateFixedLevelCleared(); break;
            case GAME_OVER: updateFixedGameOver(); break;
        }
    }
    
    public void draw(Graphics2D g)  {    g.drawImage(image, 0, 0, null); }

    protected void updateInitializing() {   }

    protected void updateOLPresents()   {   }

    protected void updateTitle()    {  }

    protected void updateLevelSelect()  {   }

    protected void updateReady()    {  }

    protected void updatePlaying()  {   }

    protected void updateLevelCleared() {   }

    protected void updateGameOver() {   }

    protected void updateFixedInitializing()    {   }

    protected void updateFixedOLPresents()  {    }

    protected void updateFixedTitle()   {   }

    protected void updateFixedLevelSelect() {   }

    protected void updateFixedReady()   {   }

    protected void updateFixedPlaying() {   }

    protected void updateFixedLevelCleared()    {   }

    protected void updateFixedGameOver()    {   }
    
    public void gameStateChanged(GameState newGameState)    {   }
    
    protected void setCurrentWaitTime() {   waitTime = System.currentTimeMillis();  }
    
    protected boolean checkPassedTime(double time)  {   return (System.currentTimeMillis() - waitTime) * 0.001 >= time; }
}