package ctr;
import ctr.Scene.GameState;
import ctr.ui.TextField;
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
    private TextField textFieldFocused = null;

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
            case OL_PRESENTS: updateOLPresents(); break;
            case TITLE: updateTitle(); break;
            case LEVEL_SELECT: updateLevelSelect(); break;
            case READY: updateReady(); break;
            case PLAYING: updatePlaying(); break;
            case LEVEL_CLEARED: updateLevelCleared(); break;
            case GAME_OVER: updateGameOver(); break;
            
            case MENU_PRINCIPAL: updateMenuPrincipal(); break;
            case LOGIN: updateLogin(); break;
            case REGISTER: updateRegister(); break;
            case PERFIL: updatePerfil(); break;
            case REACTIVATE_ACCOUNT: updateReactivateAccount(); break;
            case MENU_SESION: updateMenuSesion(); break;
            case AVATAR_SELECTOR: updateAvatarSelector(); break;
            case AUDIO_CONFIG: updateAudioConfig(); break;
            case AMIGOS_LIST: updateAmigosList(); break;
            case CHALLENGE_SELECT: updateChallengeSelect(); break;
            case STATS: updateStats(); break;

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
    
    protected void updateMenuPrincipal() { }

    protected void updateLogin() { }

    protected void updateRegister() { }

    protected void updatePerfil() { }

    protected void updateReactivateAccount() {}

    protected void updateMenuSesion() {}

    protected void updateAvatarSelector() {}

    protected void updateAudioConfig() {}

    protected void updateAmigosList() {}

    protected void updateChallengeSelect() {}

    protected void updateStats() {}
    
    public void gameStateChanged(GameState newGameState)    {   }
    
    protected void setCurrentWaitTime() {   waitTime = System.currentTimeMillis();  }
    
    protected boolean checkPassedTime(double time)  {   return (System.currentTimeMillis() - waitTime) * 0.001 >= time; }
}