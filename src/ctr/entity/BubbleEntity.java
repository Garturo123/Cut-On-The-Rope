package ctr.entity;

import ctr.Animation;
import ctr.Entity;
import ctr.Scene;
import ctr.model.Bubble;
import ctr.model.BubbleListener;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public class BubbleEntity extends Entity implements BubbleListener 
{
    private Bubble bubble;
    private AffineTransform transform = new AffineTransform();
    private Animation animation;
    private boolean burst;
    private Point burstPosition = new Point();
    
    public BubbleEntity(Scene scene, Bubble bubble) 
    {
        super(scene);
        this.bubble = bubble;
        bubble.addListener(this);
        loadImageFromResource("/res/bubble2.png");
        animation = new Animation();
        loadAllAnimations();
    }
    
    private void loadAllAnimations() 
    {
        animation.addFrames("flight", "bubble_flight", 0, 13, 0, 0, true, 20);
        animation.addFrames("burst", "bubble_pop", 0, 11, 0, 0, false, 40);
    }

    @Override
    public void updatePlaying() 
    {
        if(bubble.getCandy() != null || burst) 
            animation.update();
        if(burst && animation.isFinished())
            burst = false;
    }

    @Override
    public void draw(Graphics2D g) 
    {
        if(burst)
        {
            AffineTransform originalTransform = g.getTransform();
            g.translate(burstPosition.x, burstPosition.y);
            g.scale(1.2, 1.2);
            g.translate(-63, -58);
            animation.draw(g);
            g.setTransform(originalTransform);
            return;
        }
        if(!bubble.isVisible())
            return;
        if(bubble.getCandy() != null) 
        {
            AffineTransform originalTransform = g.getTransform();
            g.translate(bubble.getPosition().x, bubble.getPosition().y);
            g.scale(1.3, 1.3);
            g.translate(-37, -36);
            animation.draw(g);
            g.setTransform(originalTransform);
        }
        else 
        {
            transform.setToIdentity();
            transform.translate(bubble.getPosition().x, bubble.getPosition().y);
            transform.translate(-image.getWidth() / 2, -image.getHeight() / 2);
            g.drawImage(image, transform, null);
        }
    }

    @Override
    public void gameStateChanged(Scene.GameState newGameState)  {   visible = newGameState == Scene.GameState.PLAYING;  }    

    @Override
    public void onBurst() 
    {
        scene.reproducirSFX("SoundEfects/bubble_break.wav");
        burst = true;
        burstPosition.setLocation(bubble.getPosition().x, bubble.getPosition().y);
        animation.selectAnimation("burst");
    }

    @Override
    public void onCandyCaught() 
    {
        scene.reproducirSFX("SoundEfects/bubble.wav");
        animation.selectAnimation("flight");
    }
}
