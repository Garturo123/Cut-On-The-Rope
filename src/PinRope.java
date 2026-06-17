package ctr.model;

import ctr.physics.Particle;
import ctr.physics.Vec2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class PinRope 
{
    private final Model model;
    private final Vec2 position = new Vec2();
    private final double radius;
    private final double ropeLength; 
    private final Vec2 vTmp = new Vec2();
    private final List<PinRopeListener> listeners = new ArrayList<PinRopeListener>();
    private Particle p;
    private Rope rope;

    public PinRope(Model model, double x, double y, double radius, double ropeLength) 
    {
        this.model = model;
        this.position.set(x, y);
        this.radius = radius;
        this.ropeLength = ropeLength;
    }

    public Model getModel() {   return model;   }

    public Vec2 getPosition()   {   return position;    }

    public double getRadius()   {   return radius;  }

    public Particle getP()  {   return p;   }

    public Rope getRope()   {   return rope;    }

    public void addListener(PinRopeListener listener)   {   listeners.add(listener);    }
    
    public void update() 
    {
        if(rope == null) 
        {
            Vec2 candyPivot = model.getCandy().getPivot();
            vTmp.set(candyPivot);
            vTmp.sub(position);
            if(vTmp.getSize() <= (radius + model.getCandy().getRadius()) && model.getCandy().isVisible()) 
            {
                rope = model.createRope(position.x, position.y, position.x, position.y + ropeLength);
                p = rope.getFirstParticle();
                p.setPinned(true);
                rope.attach(model.getCandy(), 0);
                fireOnRopeCreated();
            }
        }
    }
    
    public void drawDebug(Graphics2D g) 
    {
        g.setColor(Color.PINK);
        g.fillOval((int) (position.x - 3), (int) (position.y - 3), 6, 6);
        g.drawOval((int) (position.x - radius), (int) (position.y - radius), (int) (2 * radius), (int) (2 * radius));
    }
    
    private void fireOnRopeCreated() 
    {
        for(PinRopeListener listener : listeners)
            listener.onRopeCreated(rope);
    }   
}