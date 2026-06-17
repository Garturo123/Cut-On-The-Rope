package ctr.model;
import ctr.physics.Vec2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Star 
{    
    private final Model model;
    private final Vec2 position = new Vec2();
    private double radius;
    private boolean visible = true;
    private final List<StarListener> listeners = new ArrayList<StarListener>();
    private final Vec2 vTmp = new Vec2();
    
    public Star(Model model, double x, double y, double radius) 
    {
        this.model = model;
        this.radius = radius;
        this.position.set(x, y);
    }

    public Model getModel() {   return model;   }

    public Vec2 getPosition()   {   return position;    }

    public double getRadius()   {   return radius;  }

    public boolean isVisible()  {   return visible; }

    public void addListener(StarListener listener)  {   listeners.add(listener);    }
    
    public void update() 
    {
        if(!model.isLevelCleared()) 
        {
            Vec2 candyPivot = model.getCandy().getPivot();
            vTmp.set(candyPivot);
            vTmp.sub(position);
            if(visible && vTmp.getSize() <= (radius + model.getCandy().getRadius())) 
            {
                visible = false;
                fireOnStarCaught();
            }
        }
    }
    
    public void drawDebug(Graphics2D g) 
    {
        g.setColor(Color.YELLOW);
        g.fillOval((int) (position.x - radius), (int) (position.y - radius), (int) (2 * radius), (int) (2 * radius));
    }

    private void fireOnStarCaught() 
    {
        for(StarListener listener : listeners)
            listener.onStarCaught();
    }    
}