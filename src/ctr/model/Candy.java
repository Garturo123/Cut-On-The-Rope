package ctr.model;
import ctr.physics.Particle;
import ctr.physics.Stick;
import ctr.physics.Vec2;
import ctr.physics.World;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Candy 
{
    private final Model model;
    private final Vec2 position = new Vec2();
    private double radius;
    private final Vec2 pivot = new Vec2();
    private boolean visible = true;
    private Particle[] particles = new Particle[4];
    private Stick[] sticks = new Stick[6];
    private final List<Rope> attachedRopes = new ArrayList<Rope>();
    private final Vec2 vTmp = new Vec2();
    private final List<CandyListener> listeners = new ArrayList<CandyListener>();
    private boolean destroyed = false;
    private final List<Rope> attachedRopesTmp = new ArrayList<Rope>();
    
    public Candy(Model model, double x, double y, double radius) 
    {
        this.model = model;
        this.position.set(x, y);
        this.radius = radius;
        create();
    }
    
    public void addListener(CandyListener listener) {   listeners.add(listener);    }

    public Model getModel() {   return model;   }

    public Vec2 getPosition()   {   return position;    }

    public double getRadius()   {   return radius;  }

    public boolean isDestroyed()    {   return destroyed;   }

    public Vec2 getPivot() 
    {
        pivot.set(particles[1].position);
        pivot.sub(particles[3].position);
        pivot.scale(0.5);
        pivot.add(particles[3].position);
        return pivot;
    }

    public void setPivot(double x, double y) 
    {
        getPivot();
        pivot.x = x - pivot.x;
        pivot.y = y - pivot.y;
        for (Particle p : particles) 
        {
            p.position.x += pivot.x;
            p.position.y += pivot.y;
        }
    }
    
    public double getAngle() 
    {
        vTmp.set(particles[1].position);
        vTmp.sub(particles[3].position);
        return Math.atan2(vTmp.y, vTmp.x);
    }

    public boolean isVisible()  {   return visible; }

    void setVisible(boolean visible)    {   this.visible = visible; }
        
    public Particle[] getPoints()   {   return particles;   }

    public Stick[] getSticks()  {   return sticks;  }

    List<Rope> getAttachedRopes()   {   return attachedRopes;   }
        
    public void detachAllRopes() 
    {
        attachedRopesTmp.clear();
        attachedRopesTmp.addAll(attachedRopes);
        for (Rope rope : attachedRopesTmp)
            rope.dettachCandy();
    }
    
    private void create() 
    {
        World world = model.getWorld();
        world.addParticle(particles[0] = new Particle(world, position.x, position.y));
        world.addParticle(particles[1] = new Particle(world, position.x + radius, position.y + radius));
        world.addParticle(particles[2] = new Particle(world, position.x, position.y + 2 * radius));
        world.addParticle(particles[3] = new Particle(world, position.x - radius, position.y + radius));
        world.addStick(sticks[0] = new Stick(particles[0], particles[1], 0, true));
        world.addStick(sticks[1] = new Stick(particles[1], particles[2], 0, true));
        world.addStick(sticks[2] = new Stick(particles[2], particles[3], 0, true));
        world.addStick(sticks[3] = new Stick(particles[3], particles[0], 0, true));
        world.addStick(sticks[4] = new Stick(particles[0], particles[2], 0, true));
        world.addStick(sticks[5] = new Stick(particles[1], particles[3], 0, true));
    }

    public void update() 
    {
        if(!model.isPlaying())
            return;
        Vec2 candyPivot = getPivot();
        if(!model.isLevelFailured() && ((candyPivot.y < (3 * -radius) || candyPivot.y > (model.getWorld().getHeight() + 3 * radius))))
            model.levelFailured();
    }
    
    public void drawDebug(Graphics2D g) 
    {
        AffineTransform at = g.getTransform();
        g.translate(getPivot().x, getPivot().y);
        g.rotate(getAngle());
        g.setColor(Color.MAGENTA);
        g.drawOval((int) (-radius), (int) (-radius), (int) (2 * radius), (int) (2 * radius));
        g.drawLine(0, 0, (int) radius, 0);
        g.setTransform(at);
    }
    
    public void addForce(Vec2 force) 
    {
        for (Particle p : particles)
            p.addForce(force);
    }

    public void dampenVelocity(double scaleX, double scaleY)
    {
        for (Particle p : particles)
        {
            double velocityX = p.position.x - p.previousPosition.x;
            double velocityY = p.position.y - p.previousPosition.y;
            p.previousPosition.x = p.position.x - velocityX * scaleX;
            p.previousPosition.y = p.position.y - velocityY * scaleY;
        }
    }

    public double getAverageVelocityY()
    {
        double velocityY = 0;
        for (Particle p : particles)
            velocityY += p.position.y - p.previousPosition.y;
        return velocityY / particles.length;
    }

    public void limitNextUpwardVelocity(double maxUpwardSpeed)
    {
        double maxVelocityY = -maxUpwardSpeed;
        for (Particle p : particles)
        {
            double velocityY = p.position.y - p.previousPosition.y;
            double projectedVelocityY = velocityY + p.force.y;
            if (projectedVelocityY < maxVelocityY)
                p.previousPosition.y = p.position.y - (maxVelocityY - p.force.y);
        }
    }
    
    public void destroy() 
    {
        if (destroyed)
            return;
        destroyed = true;
        visible = false;
        detachAllRopes();
        fireOnCandyDestroyed();
        model.levelFailured();
    }
    
    private void fireOnCandyDestroyed() 
    {
        for (CandyListener listener : listeners)
            listener.onCandyDestroyed();
    }    
}
