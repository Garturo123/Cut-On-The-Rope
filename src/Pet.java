package ctr.model;
import ctr.physics.Vec2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Pet 
{    
    private final Model model;
    private final Vec2 position = new Vec2();
    private double radius;
    private final List<PetListener> listeners = new ArrayList<PetListener>();
    private final Vec2 vTmp = new Vec2();
    private final double closeDistance;
    private boolean candyClose;
    
    public Pet(Model model, double x, double y, double radius, double closeDistance) 
    {
        this.model = model;
        this.radius = radius;
        this.position.set(x, y);
        this.closeDistance = closeDistance;
    }

    public Model getModel() {   return model;   }

    public Vec2 getPosition()   {   return position;    }

    public double getRadius()   {   return radius;  }

    public void addListener(PetListener listener)   {   listeners.add(listener);    }
    
    public void update() 
    {
        if(!model.isLevelCleared()) 
        {
            Vec2 candyPivot = model.getCandy().getPivot();
            vTmp.set(candyPivot);
            vTmp.sub(position);
            if(vTmp.getSize() <= radius && !model.getCandy().isDestroyed()) 
            {
                fireOnCandyEaten();
                model.levelCleared();
            }
            else if(!candyClose && vTmp.getSize() <= (radius + closeDistance)) 
            {
                candyClose = true;
                fireOnCandyClose();
            }
            else if(candyClose && vTmp.getSize() > (radius + closeDistance)) 
            {
                candyClose = false;
                fireOnCandyEscaped();
            }
        }
    }
    
    public boolean isCandyAbove() 
    {
        Vec2 candyPivot = model.getCandy().getPivot();
        return Math.abs(candyPivot.x - position.x) < (1.5 * radius) && candyPivot.y < position.y;
    }
    
    public void drawDebug(Graphics2D g) 
    {
        g.setColor(Color.GRAY);
        g.drawOval((int) (position.x - radius), (int) (position.y - radius), (int) (2 * radius), (int) (2 * radius));
        g.drawOval((int) (position.x - (radius + closeDistance)), (int) (position.y - (radius + closeDistance)), (int) (2 * (radius + closeDistance)), (int) (2 * (radius + closeDistance)));
    }

    private void fireOnCandyClose() 
    {
        for(PetListener listener : listeners) 
            listener.onCandyClose();
    }    
    
    private void fireOnCandyEscaped() 
    {
        for (PetListener listener : listeners)
            listener.onCandyEscaped();
    }    
    
    private void fireOnCandyEaten() 
    {
        for (PetListener listener : listeners)
            listener.onCandyEaten();
    }    
}