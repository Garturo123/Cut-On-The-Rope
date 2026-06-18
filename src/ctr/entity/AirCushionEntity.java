package ctr.entity;
import ctr.Animation;
import ctr.Entity;
import ctr.Scene;
import ctr.model.AirCushion;
import ctr.model.AirCushionListener;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class AirCushionEntity extends Entity implements AirCushionListener 
{
    
    private AirCushion airCushion;
    private Animation animation;
    
    public AirCushionEntity(Scene scene, AirCushion airCushion) 
    {
        super(scene);
        this.airCushion = airCushion;
        airCushion.addListener(this);
        animation = new Animation();
        loadAllAnimations();
    }
    
    private void loadAllAnimations() 
    {
        animation.addFrames("pump", "pump", 0, 5, 0, 0, false, 8);
        animation.selectAnimation("pump");
        animation.setCurrentFrame(5);
    }
    
    @Override
    public void update()    {   animation.update(); }

    @Override
    public void draw(Graphics2D g) 
    {
        AffineTransform originalTransform = g.getTransform();
        g.translate(airCushion.getPosition().x, airCushion.getPosition().y);
        g.rotate((1 + airCushion.getDirection()) * Math.toRadians(90));
        g.translate(-48, -64);
        animation.draw(g);
        g.setTransform(originalTransform);
    }
    
    @Override
    public void gameStateChanged(Scene.GameState newGameState)  {   visible = newGameState == Scene.GameState.PLAYING;  }    

    @Override
    public void onAirCushionFire()
    {
        int pump = 1 + (int) (Math.random() * 4);
        scene.reproducirSFX("SoundEfects/pump_" + pump + ".wav");
        animation.selectAnimation("pump");
    }
}
