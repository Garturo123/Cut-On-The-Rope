package ctr;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

public class View extends Canvas
{
    public static final int SCREEN_WIDTH = 900;
    public static final int SCREEN_HEIGHT = 600;
    private BufferStrategy bs;
    private Scene scene;
    
    public View() 
    {
        setBackground(Color.BLACK);
        Mouse mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        scene = new Scene();
    }
    
    public void start()
    {
        scene.start();
        createBufferStrategy(2);
        bs = getBufferStrategy();
        new Thread(new MainLoop()).start();
    }
    
    public void update()    {   scene.update(); }

    public void updateFixed()   {   scene.updateFixed();    }
    
    public void draw(Graphics2D g){    scene.draw(g);   }
    
    private class MainLoop implements Runnable
    {
        @Override
        public void run()
        {
            boolean running = true;
            while(running)
            {
                Time.update();
                update();
                while (Time.needsUpdate())
                    updateFixed();
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                g.setBackground(Color.BLACK);
                g.clearRect(0, 0, getWidth(), getHeight());
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                draw(g);
                g.dispose();
                bs.show();
                try 
                {
                    Thread.sleep(5);
                } catch (InterruptedException ex)   { }
            }
        }
    }
}