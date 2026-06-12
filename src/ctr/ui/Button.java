package ctr.ui;
import ctr.Entity;
import ctr.FontRenderer;
import ctr.Mouse;
import ctr.Scene;
import ctr.physics.Vec2;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Button extends Entity 
{
    private BufferedImage imageOn;
    private BufferedImage imageOff;
    private BufferedImage imageOver;
    private boolean pressed;
    private boolean over;
    private String text;
    private final Vec2 position = new Vec2();
    private final Rectangle rectangle = new Rectangle();
    private ButtonListener listener;
    
    public Button(Scene scene, String text, int textX, int textY, int buttonX, int buttonY) 
    {
        super(scene);
        imageOn = loadImageFromResource("/res/button_on.png");
        imageOff = loadImageFromResource("/res/button_off.png");
        imageOver = loadImageFromResource("/res/button_over.png");
        renderText((Graphics2D) imageOn.getGraphics(), text, textX, textY);
        renderText((Graphics2D) imageOff.getGraphics(), text, textX, textY);
        renderText((Graphics2D) imageOver.getGraphics(), text, textX, textY);
        position.set(buttonX, buttonY);
        rectangle.setBounds(buttonX, buttonY, image.getWidth(), image.getHeight());
        visible = true;
    }

    private void renderText(Graphics2D g, String text, int textX, int textY) 
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontRenderer.draw(g, text, textX, textY);
    }
    
    public ButtonListener getListener() {   return listener;    }

    public void setListener(ButtonListener listener)    {   this.listener = listener;   }
    
    public void reset() {   pressed = false;    }
    
    @Override
    public void update()
    {
        if(!visible) 
        {
            over = false;
            pressed = false;
            return;
        }
        over = rectangle.contains(Mouse.x, Mouse.y);
        if(over && !Mouse.pressedConsumed && Mouse.pressed)
        {
            Mouse.pressedConsumed = true;
            pressed = true;
            if(listener != null)
                listener.onClick();
        }
    }
    public void setText(String text) {
        this.text = text;
    }
    @Override
    public void draw(Graphics2D g) 
    {
        if(pressed)
            g.drawImage(imageOn, (int) position.x, (int) position.y, null);
        else if (over)
            g.drawImage(imageOver, (int) position.x, (int) position.y, null);
        else
            g.drawImage(imageOff, (int) position.x, (int) position.y, null);
    }
}