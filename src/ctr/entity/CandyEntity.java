package ctr.entity;
import ctr.Entity;
import ctr.Scene;
import ctr.model.Candy;
import ctr.model.CandyListener;
import ctr.physics.Particle;
import ctr.physics.Vec2;
import ctr.physics.World;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class CandyEntity extends Entity implements CandyListener 
{
    private Candy candy;
    private AffineTransform transform = new AffineTransform();
    private BufferedImage candyLeft;
    private BufferedImage candyRight;
    private Particle candyLeftParticle;
    private Particle candyRightParticle;
    private boolean destroyed;
    private double angle;
    
    public CandyEntity(Scene scene) 
    {
        super(scene);
        this.candy = scene.getModel().getCandy();
        candy.addListener(this);
        candyLeft = loadImageFromResource("/res/candy_left.png");
        candyRight = loadImageFromResource("/res/candy_right.png");
        loadImageFromResource("/res/candy.png");
    }

    @Override
    public void updatePlaying() 
    {
        if(destroyed)
            angle += 0.2;
    }

    @Override
    public void draw(Graphics2D g) 
    {
        if(candy.isVisible()) 
        {
            transform.setToIdentity();
            transform.translate(candy.getPivot().x, candy.getPivot().y);
            transform.rotate(candy.getAngle());
            transform.translate(-27, -27);
            g.drawImage(image, transform, null);
        }
        else if(destroyed) 
        {
            transform.setToIdentity();
            transform.translate(candyLeftParticle.position.x, candyLeftParticle.position.y);
            transform.rotate(angle);
            transform.translate(-27, -27);
            g.drawImage(candyLeft, transform, null);
            transform.setToIdentity();
            transform.translate(candyRightParticle.position.x, candyRightParticle.position.y);
            transform.rotate(-angle);
            transform.translate(-27, -27);
            g.drawImage(candyRight, transform, null);
        }
    }

    @Override
    public void gameStateChanged(Scene.GameState newGameState)  {   visible = newGameState == Scene.GameState.PLAYING;  }    

    @Override
    public void onCandyDestroyed() 
    {
        destroyed = true;
        World world = scene.getModel().getWorld();
        double x = candy.getPivot().x;
        double y = candy.getPivot().y;
        candyLeftParticle = new Particle(world, x, y);
        candyRightParticle = new Particle(world, x, y);
        world.addParticle(candyLeftParticle);
        world.addParticle(candyRightParticle);
        candyLeftParticle.addForce(new Vec2(-10, 0));
        candyRightParticle.addForce(new Vec2(10, 0));
    } 
}