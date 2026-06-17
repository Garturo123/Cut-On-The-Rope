package ctr.entity;
import ctr.Animation;
import ctr.Entity;
import ctr.Scene;
import ctr.model.Star;
import ctr.model.StarListener;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class StarEntity extends Entity implements StarListener 
{
    private Star star;
    private AffineTransform transform = new AffineTransform();
    private BufferedImage bloom;
    private double bloomStartValue;
    private double bloomScale;
    private Animation animation;
    
    public StarEntity(Scene scene, Star star) 
    {
        super(scene);
        this.star = star;
        star.addListener(this);
        bloom = loadImageFromResource("/res/star_bloom.png");
        bloomStartValue = 999 * Math.random();
        loadImageFromResource("/res/star2.png");
        animation = new Animation();
        loadAllAnimations();
    }

    private void loadAllAnimations() 
    {
        int x = (int) star.getPosition().x - 15;
        int y = (int) star.getPosition().y - 15;
        animation.addFrames("idle", "star_idle", 0, 17, x, y, true, 0.01);
        x = (int) star.getPosition().x - 71;
        y = (int) star.getPosition().y - 75;
        animation.addFrames("disappear", "star_disappear", 0, 10, x, y, false, 20);
        animation.selectAnimation("idle");
    }

    @Override
    public void update() 
    {
        animation.update();
        bloomScale = 1.4 + 0.2 * Math.sin(bloomStartValue + System.nanoTime() * 0.000000005);
    }

    @Override
    public void draw(Graphics2D g) 
    {
        if(!star.isVisible()) 
        {
            animation.draw(g);
            return;
        }
        transform.setToIdentity();
        transform.translate(star.getPosition().x, star.getPosition().y);
        transform.scale(bloomScale, bloomScale);
        transform.translate(-bloom.getWidth() / 2, -bloom.getHeight() / 2);
        g.drawImage(bloom, transform, null);
        animation.draw(g);
    }

    @Override
    public void gameStateChanged(Scene.GameState newGameState)  {   visible = newGameState == Scene.GameState.PLAYING;  }    

    @Override
    public void onStarCaught()
    {
        int caught = 0;
        for (Star levelStar : scene.getModel().getStars()) {
            if (!levelStar.isVisible())
                caught++;
        }
        int soundIndex = Math.max(1, Math.min(3, caught));
        scene.reproducirSFX("SoundEfects/star_" + soundIndex + ".wav");
        animation.selectAnimation("disappear");
    }
}
