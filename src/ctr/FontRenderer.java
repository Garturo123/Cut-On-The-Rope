package ctr;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FontRenderer 
{
    private static Font font;
    
    static 
    {
        try 
        {
            try (InputStream is = ResourceLoader.open("/res/GOODDP.TTF")) {
                font = Font.createFont(Font.TRUETYPE_FONT, is);
                font = font.deriveFont(40f);
            }
        } catch (Exception ex) 
        {
            Logger.getLogger(FontRenderer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    public static void draw(Graphics2D g, String text, int x, int y) 
    {
        g.setFont(font);
        AffineTransform tranform = new AffineTransform();
        tranform.translate(x, y);
        TextLayout layout = new TextLayout(text, font, g.getFontRenderContext());
        Shape outline = layout.getOutline(tranform);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(4f));
        g.draw(outline);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }
}
