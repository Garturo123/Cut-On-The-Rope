package ctr.model;

import ctr.physics.Line;
import ctr.physics.Vec2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Spikes 
{    
    private final Model model;
    private final Polygon polygon = new Polygon();
    private final Line line = new Line(0, 0, 0, 0);
    private final Rectangle rectangle = new Rectangle();
    
    public Spikes(Model model, int x, int y, int w, int h) 
    {
        this.model = model;
        polygon.addPoint(x, y);
        polygon.addPoint(x + w, y);
        polygon.addPoint(x + w, y + h);
        polygon.addPoint(x, y + h);
        rectangle.setBounds(x, y, w, h);
    }

    public Polygon getPolygon() {   return polygon; }

    public Rectangle getRectangle() {   return rectangle;   }
    
    public void update() 
    {
        for(int i = 0; i < polygon.npoints; i++) 
        {
            line.getA().set(polygon.xpoints[i], polygon.ypoints[i]);
            int nextIndex = (i + 1) % polygon.npoints;
            line.getB().set(polygon.xpoints[nextIndex], polygon.ypoints[nextIndex]);
            Vec2 candyPivot = model.getCandy().getPivot();
            if(polygon.contains(candyPivot.x, candyPivot.y) || line.intersectsWithCircle(candyPivot, model.getCandy().getRadius())) 
            {
                model.getCandy().destroy();
                return;
            }
        }
    }
    
    public void drawDebug(Graphics2D g) 
    {
        g.setColor(Color.PINK);
        g.draw(polygon);
    }
    
}
