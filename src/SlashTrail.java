package ctr.model;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class SlashTrail 
{
    private Color color = Color.WHITE;
    private final List<Point> trail = new ArrayList<Point>();
    private final int size;
    private final Stroke[] strokes;
    private boolean visible;
    
    public SlashTrail(int size) {   this(size, 1);  }
    
    public SlashTrail(int size, double scale) 
    {
        this.size = size;
        strokes = new Stroke[size];
        createStrokes(scale);
    }

    public Color getColor() {   return color;   }

    public void setColor(Color color)   {   this.color = color; }

    public List<Point> getTrail()   {   return trail;   }

    public Stroke[] getStrokes()    {   return strokes; }

    public boolean isVisible()  {   return visible; }

    private void createStrokes(double scale) 
    {
        for(int i = 0; i < strokes.length; i++)
            strokes[i] = new BasicStroke((float) (1 + i * scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
    
    public int getSize()    {   return size; }
    
    public void addTrail(int x, int y) 
    {
        if(x < 0 || y < 0)
            trail.add(null);
        else 
            trail.add(new Point(x, y));
        while(trail.size() > size)
            trail.remove(0);
        visible = true;
    }

    public void clear() {   trail.clear();  }
    
    public void drawDebug(Graphics2D g) 
    {
        if(!visible)
            return;
        Stroke originalStroke = g.getStroke();
        for(int i = 0; i < trail.size() - 1; i++) 
        {
            g.setStroke(strokes[i]);
            Point p1 = trail.get(i);
            Point p2 = trail.get(i + 1);
            if(p1 != null && p2 != null) 
            {
                g.setColor(color);
                g.drawLine(p1.x, p1.y, p2.x + 1, p2.y + 1);
            }
        }
        g.setStroke(originalStroke);
    }
}