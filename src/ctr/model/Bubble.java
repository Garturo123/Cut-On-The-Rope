package ctr.model;
import ctr.physics.Vec2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Bubble 
{
    private static final double FOLLOW_X_STRENGTH = 0.035;
    private static final double FOLLOW_Y_STRENGTH = 0.12;
    private static final double FREE_FOLLOW_Y_STRENGTH = 0.24;
    private static final double HORIZONTAL_VELOCITY_DAMPING = 0.10;
    private static final double FALLING_VELOCITY_DAMPING = 0.20;
    private static final double RISING_VELOCITY_DAMPING = 0.82;
    private static final double FREE_RISING_VELOCITY_DAMPING = 1.0;
    private static final double FREE_RISE_FORCE = -1.05;
    private static final double HOLD_RISE_FORCE = -0.5;
    private static final double TARGET_RISE_SPEED = 4.5;
    private static final double ANCHOR_FORCE_X = 0.08;
    private static final double ANCHOR_FORCE_Y = 0.05;
    private static final double MAX_ANCHOR_FORCE_X = 0.8;
    private static final double MAX_ANCHOR_FORCE_Y = 1.2;

    private final Model model;
    private final Vec2 position = new Vec2();
    private double radius;
    private boolean visible = true;
    private Candy candy;
    private final Vec2 upForce;
    private final Vec2 anchorForce = new Vec2();
    private final Vec2 vTmp = new Vec2();
    private final List<BubbleListener> listeners = new ArrayList<BubbleListener>();
    
    public Bubble(Model model, double x, double y, double radius) 
    {
        this.model = model;
        this.radius = radius;
        this.position.set(x, y);
        this.upForce = new Vec2(0, -1);
    }

    public Model getModel() {   return model;   }

    public Vec2 getPosition()   {   return position;    }

    public double getRadius()   {   return radius;  }

    public boolean isVisible()  {   return visible; }

    public Candy getCandy() {   return candy;   }
    
    public void addListener(BubbleListener listener)    {   listeners.add(listener);    }
    
    public void update()   
    {
        if(!visible)
            return;
        else if(candy != null) 
        {
            if(!candy.isVisible()) 
            {
                burst();
                return;
            }
            boolean freeCandy = candy.getAttachedRopes().isEmpty();
            double verticalVelocity = candy.getAverageVelocityY();
            if(freeCandy)
                upForce.y = verticalVelocity > -TARGET_RISE_SPEED ? FREE_RISE_FORCE : HOLD_RISE_FORCE;
            else if(verticalVelocity > 0.1)
                upForce.y -= 0.02;
            candy.addForce(upForce);
            Vec2 candyPivot = candy.getPivot();
            double fallingDamping = verticalVelocity > 0.1
                ? FALLING_VELOCITY_DAMPING
                : (freeCandy ? FREE_RISING_VELOCITY_DAMPING : RISING_VELOCITY_DAMPING);
            candy.dampenVelocity(HORIZONTAL_VELOCITY_DAMPING, fallingDamping);
            double anchorY = clamp((position.y - candyPivot.y) * ANCHOR_FORCE_Y, -MAX_ANCHOR_FORCE_Y, MAX_ANCHOR_FORCE_Y);
            if(freeCandy && verticalVelocity <= 0 && anchorY > 0)
                anchorY = 0;
            anchorForce.set(
                clamp((position.x - candyPivot.x) * ANCHOR_FORCE_X, -MAX_ANCHOR_FORCE_X, MAX_ANCHOR_FORCE_X),
                anchorY
            );
            candy.addForce(anchorForce);
            if(freeCandy)
                candy.limitNextUpwardVelocity(TARGET_RISE_SPEED);
            position.x += (candyPivot.x - position.x) * FOLLOW_X_STRENGTH;
            position.y += (candyPivot.y - position.y) * (freeCandy ? FREE_FOLLOW_Y_STRENGTH : FOLLOW_Y_STRENGTH);
        }
        else 
        {
            Vec2 candyPivot = model.getCandy().getPivot();
            vTmp.set(candyPivot);
            vTmp.sub(position);
            if (candy == null && vTmp.getSize() <= radius + model.getCandy().getRadius() && model.getCandy().isVisible()) 
            {
                candy = model.getCandy();
                upForce.set(0, -1);
                fireOnCandyCaught();
            }
        }
    }
    
    public void tryToBurst(double x, double y) 
    {
        if(!visible)
            return;
        vTmp.set(x, y);
        vTmp.sub(position);
        if(model.isPlaying() && candy != null && vTmp.getSize() <= radius)
            burst();
    }
    
    private void burst() 
    {
        visible = false;
        fireOnBurst();
    }
    
    public void drawDebug(Graphics2D g) 
    {
        AffineTransform at = g.getTransform();
        g.translate(position.x, position.y);
        g.setColor(Color.CYAN);
        g.drawOval((int) (-radius), (int) (-radius), (int) (2 * radius), (int) (2 * radius));
        g.setTransform(at);
    }

    private void fireOnBurst() 
    {
        for(BubbleListener listener : listeners)
            listener.onBurst();
    }

    private void fireOnCandyCaught() 
    {
        for (BubbleListener listener : listeners)
            listener.onCandyCaught();
    }

    private double clamp(double value, double min, double max)
    {
        return Math.max(min, Math.min(max, value));
    }
}
