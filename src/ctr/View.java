package ctr;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class View extends Canvas implements KeyListener  // Implementar KeyListener
{
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    private BufferStrategy bs;
    private Scene scene;
    
    public View() 
    {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        
        // Mouse listeners
        Mouse mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        
        // Keyboard listener
        addKeyListener(this);  // Agregar KeyListener
        setFocusable(true);     // Importante: hacer focusable el canvas
        requestFocus();         // Solicitar foco para recibir eventos de teclado
        
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
    
    // Implementación de KeyListener
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();
        scene.procesarEntradaTeclado(keyCode, keyChar);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // No es necesario para este caso, pero debe implementarse
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // También podemos procesar aquí si es necesario
        // Pero keyPressed es suficiente
    }
    
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