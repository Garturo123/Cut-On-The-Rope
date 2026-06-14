package ctr.entity;
import ctr.Entity;
import ctr.Scene;
import ctr.View;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CurtainEntity extends Entity 
{    
    private BufferedImage top;
    private BufferedImage bottom;   
    private double currentP = 1;
    private double targetP = 1;
    private double topY;
    private double bottomY;
    
    public CurtainEntity(Scene scene)   {   super(scene);   }

    @Override
    public void start() 
    {
        top = loadImageFromResource("/res/curtain_top.png");
        bottom = loadImageFromResource("/res/curtain_bottom.png");
    }

    @Override
    public void update() 
    {
        double dif = targetP - currentP;
        int s = dif > 0 ? 1 : -1;
        if (dif > 0.01 || dif < -0.01)
            currentP = currentP + s * 0.04;
        else
            currentP = targetP;
        topY = -View.SCREEN_HEIGHT / 2 * currentP;
        bottomY = View.SCREEN_HEIGHT / 2 + View.SCREEN_HEIGHT / 2 * currentP;
        visible = currentP < 1;
    }

    @Override
    public void draw(Graphics2D g) 
    {
        g.drawImage(top, 0, (int) topY, null);
        g.drawImage(bottom, 0, (int) bottomY, null);
    }
    
    public void open()  {   targetP = 1;    }

    public void close() {   targetP = 0;    }

    public void openInstantly() 
    {
        targetP = 1;
        currentP = 1;
    }

    public void closeInstantly() 
    {
        targetP = 0;
        currentP = 0;
    }

    public boolean isFinished() {   return currentP == targetP; }
}