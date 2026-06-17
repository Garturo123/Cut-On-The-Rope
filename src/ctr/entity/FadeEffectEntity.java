package ctr.entity;
import ctr.Entity;
import ctr.Scene;
import ctr.Time;
import ctr.View;
import java.awt.Color;
import java.awt.Graphics2D;

public class FadeEffectEntity extends Entity 
{
    private Color[] colorsBlack = new Color[256];
    private Color[] colorsWhite = new Color[256];
    private Color color;
    private Color targetColor = Color.WHITE;
    private double alpha = 0;
    private double targetAlpha = 0;

    public FadeEffectEntity(Scene scene)    {   super(scene);   }

    public Color getTargetColor()   {   return targetColor; }

    public void setTargetColor(Color targetColor)   {   this.targetColor = targetColor; }

    public double getAlpha()    {   return alpha;   }

    public void setAlpha(double alpha)  {   this.alpha = alpha; }

    public double getTargetAlpha()  {   return targetAlpha; }

    public void setTargetAlpha(double targetAlpha)  {   this.targetAlpha = targetAlpha; }

    @Override
    public void start() 
    {
        for(int c = 0; c < colorsWhite.length; c++) 
        {
            colorsBlack[c] = new Color(0, 0, 0, c);
            colorsWhite[c] = new Color(255, 255, 255, c);
        }
        color = colorsWhite[255];
    }

    @Override
    public void update() 
    {
        double dif = targetAlpha - alpha;
        double s = dif > 0 ? 1 : -1;
        if(dif > 0.01 || dif < -0.01) 
        {
            double delta = Time.getDelta();
            alpha = alpha + s * delta * 0.5;
            alpha = alpha > 1 ? 1 : alpha;
            alpha = alpha < 0 ? 0 : alpha;
        }
        else
            alpha = targetAlpha;
        if(targetColor == Color.WHITE)
            color = colorsWhite[(int) (255 * alpha)];
        else 
            color = colorsBlack[(int) (255 * alpha)];
        visible = alpha > 0;
    }
    
    @Override
    public void draw(Graphics2D g) 
    {
        g.setColor(color);
        g.fillRect(0, 0, View.SCREEN_WIDTH, View.SCREEN_HEIGHT);
    }
    
    public void fadeIn()    {   targetAlpha = 0;    }

    public void fadeOut()   {   targetAlpha = 1;    }

    public void clear()
    {
        alpha = 0;
        targetAlpha = 0;
        visible = false;
        color = new Color(0, 0, 0, 0);
    }
    
    public boolean fadeEffectFinished() {   return alpha == targetAlpha;    }
}
