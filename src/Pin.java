package ctr.model;
import ctr.physics.Particle;
import java.awt.Color;
import java.awt.Graphics2D;

public class Pin 
{
    private final Particle p;
    public Pin(Particle p) 
    {
        this.p = p;
        p.setPinned(true);
    }

    public Pin(Particle p, double x, double y) 
    {
        this.p = p;
        p.position.set(x, y);
        p.setPinned(true);
    }
    
    public void drawDebug(Graphics2D g) 
    {
        g.setColor(Color.RED);
        g.fillOval((int) (p.position.x - 3), (int) (p.position.y), 6, 6);
    }
}
