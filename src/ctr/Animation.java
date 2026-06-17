package ctr;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Animation 
{
    private Map<String, List<BufferedImage>> frames = new HashMap<String, List<BufferedImage>>();
    private Map<String, Point> positions = new HashMap<String, Point>();
    private Map<String, Boolean> loops = new HashMap<String, Boolean>();
    private double frameRate;
    private String currentAnimationName;
    private List<BufferedImage> currentAnimation;
    private double currentFrame;
    private Point currentPosition;
    private boolean currentLoop;

    public void addFrames(String animationName, String fileBaseName, int startFrame, int endFrame, int x, int y, boolean loop) 
    {
        addFrames(animationName, fileBaseName, startFrame, endFrame, x, y, loop, 50);
    }
    
    public void addFrames(String animationName, String fileBaseName, int startFrame, int endFrame, int x, int y, boolean loop, double frameRate) 
    {
        List<BufferedImage> f = new ArrayList<BufferedImage>();
        for(int i = startFrame; i <= endFrame; i++) 
        {
            try 
            {
                BufferedImage sprite = ResourceLoader.loadImage("/res/" + fileBaseName + i + ".png");
                f.add(sprite);
            } catch (IOException ex) 
            {
                Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
        }
        frames.put(animationName, f);
        positions.put(animationName, new Point(x, y));
        loops.put(animationName, loop);
        this.frameRate = frameRate;
    }

    public String getCurrentAnimationName() {   return currentAnimationName;    }

    public List<BufferedImage> getCurrentAnimation()    {  return currentAnimation;    }

    public double getCurrentFrame() {   return currentFrame;    }

    public void setCurrentFrame(double currentFrame)    {  this.currentFrame = currentFrame;    }

    public Point getCurrentPosition()   {   return currentPosition; }

    public boolean isCurrentLoop()  {   return currentLoop; }
    
    public boolean isFinished() 
    {
        if(currentLoop)
            return false;
        else if((int) currentFrame == currentAnimation.size() - 1)
            return true;
        return false;
    }
    
    public void selectAnimation(String animationName) 
    {
        currentAnimationName = animationName;
        currentAnimation = frames.get(animationName);
        currentFrame = 0;
        currentPosition = positions.get(animationName);
        currentLoop = loops.get(animationName);
    }
    
    public void update() 
    {
        if(currentAnimation == null)
            return;
        currentFrame = (currentFrame + frameRate * Time.getDelta());
        if(currentLoop)
            currentFrame = currentFrame % currentAnimation.size();
        else
        {
            if ((int) currentFrame > currentAnimation.size() - 1)
                currentFrame = currentAnimation.size() - 1;
        }
    }
    
    public void draw(Graphics2D g) 
    {
        if (currentAnimation == null)
            return;
        BufferedImage sprite = currentAnimation.get((int) currentFrame);
        g.drawImage(sprite, currentPosition.x, currentPosition.y, null);
    }
}
