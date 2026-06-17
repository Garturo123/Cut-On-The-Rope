package ctr.model;
import ctr.physics.Line;
import ctr.physics.Particle;
import ctr.physics.Vec2;
import ctr.physics.World;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Model 
{
    private final World world;
    private final SlashTrail slashTrail;
    private final Line cutLine = new Line(0, 0, 0, 0); 
    private Pet pet;
    private Candy candy;
    private final List<Rope> ropes = new ArrayList<Rope>();
    private final List<Pin> pins = new ArrayList<Pin>();
    private final List<Bubble> bubbles = new ArrayList<Bubble>();
    private final List<Spikes> spikesList = new ArrayList<Spikes>();
    private final List<AirCushion> airCushions = new ArrayList<AirCushion>();
    private final List<PinRope> pinRopes = new ArrayList<PinRope>();
    private final List<Star> stars = new ArrayList<Star>();   
    private List<ModelListener> listeners = new ArrayList<ModelListener>();   
    private String currentLevelName;
    private final LevelLoader levelLoader;
    private boolean levelFailured;
    private boolean levelCleared;   
    private final Vec2 vTmp = new Vec2();
    
    public Model(int width, int height, int slashTrailSize) 
    {
        world = new World(width, height);
        slashTrail = new SlashTrail(slashTrailSize);
        levelLoader = new LevelLoader(this);
    }

    public World getWorld() {   return world;   }

    public SlashTrail getSlashTrail()   {   return slashTrail;  }
    
    public void addSlashTrail(int x, int y) 
    {
        slashTrail.addTrail(x, y);
        tryToCutRope();
        tryToBurstBubbles(x, y);
        tryToFireAirCushions(x, y);
    }
    
    private void tryToCutRope() 
    {
        for(int i = 0; i < slashTrail.getTrail().size() - 1; i++) 
        {
            Point p1 = slashTrail.getTrail().get(i);
            Point p2 = slashTrail.getTrail().get(i + 1);
            if(p1 != null && p2 != null) 
            {
                cutLine.getA().set(p1.x, p1.y);
                cutLine.getB().set(p2.x, p2.y);
                for(Rope rope : ropes)
                    rope.cut(cutLine);
            }
        }
    }

    private void tryToBurstBubbles(double x, double y) 
    {
        for(Bubble bubble : bubbles)
            bubble.tryToBurst(x, y);
    }

    private void tryToFireAirCushions(double x, double y) 
    {
        for(AirCushion airCushion : airCushions)
            airCushion.tryToFire(x, y);
    }

    public Pet getPet() {   return pet; }
    
    public Candy getCandy() {   return candy;   }

    public List<Rope> getRopes()    {   return ropes;   }

    public List<Pin> getPins()  {   return pins;    }

    public List<PinRope> getPinRopes()  {   return pinRopes;    }

    public List<Star> getStars()    {   return stars;   }
    
    public List<Bubble> getBubbles()    {   return bubbles; }

    public List<Spikes> getSpikesList() {   return spikesList;  }

    public List<AirCushion> getAirCushions()    {   return airCushions; }
    
    public void addListener(ModelListener listener) {   listeners.add(listener);    }

    public List<ModelListener> getListeners()   {   return listeners;   }

    public String getCurrentLevelName() {   return currentLevelName;    }

    public boolean isLevelFailured()    {   return levelFailured;   }

    public boolean isLevelCleared() {   return levelCleared;    }
    
    public boolean isPlaying()  {   return !levelCleared && !levelFailured; }
 
    public Pet createPet(double x, double y, double radius, double closeDistance)   {   return pet = new Pet(this, x, y, radius, closeDistance);    }

    public Candy createCandy(double x, double y, double radius) {   return candy = new Candy(this, x, y, radius);   }

    public Rope createRope(double x1, double y1, double x2, double y2) 
    {
        vTmp.set(x2 - x1, y2 - y1);
        double segmentSize = 15;
        int segmentsNumber = (int) (vTmp.getSize() / segmentSize);
        Rope rope = new Rope(this, x1, y1, x2, y2, segmentsNumber);
        ropes.add(rope);
        return rope;        
    }

    public Pin createPin(Particle p) 
    {
        Pin pin = new Pin(p);
        pins.add(pin);
        return pin;
    }

    public Bubble createBubble(double x, double y, double radius) 
    {
        Bubble bubble = new Bubble(this, x, y, radius);
        bubbles.add(bubble);
        return bubble;
    }

    public Spikes createSpikes(int x, int y, int w, int h) 
    {
        Spikes spikes = new Spikes(this, x, y, w, h);
        spikesList.add(spikes);
        return spikes;
    }

    public AirCushion createAirCushion(int x, int y, int direction) 
    {
        AirCushion airCushion = new AirCushion(this, x, y, direction);
        airCushions.add(airCushion);
        return airCushion;
    }

    public PinRope createPinRope(double x, double y, double radius, double ropeLength) 
    {
        PinRope pinRope = new PinRope(this, x, y, radius, ropeLength);
        pinRopes.add(pinRope);
        return pinRope;
    }

    public Star createStar(double x, double y, double radius) 
    {
        Star star = new Star(this, x, y, radius);
        stars.add(star);
        return star;
    }
    
    private void clear() 
    {
        levelCleared = false;
        levelFailured = false;
        world.clear();
        slashTrail.clear();
        pet = null;
        candy = null;
        ropes.clear();
        pins.clear();
        bubbles.clear();
        spikesList.clear();
        airCushions.clear();
        pinRopes.clear();
        stars.clear();
    }
    
    public void update() 
    {
        if (candy != null)
            candy.update();
        if (pet != null)
            pet.update();
        updateBubble();
        updateSpikes();
        updateAirCushions();
        updatePinRopes();
        updateStars();
        world.update();
    }
    
    private void updateBubble() 
    {
        for (Bubble bubble : bubbles)
            bubble.update();
    }

    private void updateSpikes() 
    {
        for (Spikes spike : spikesList)
            spike.update();
    }

    private void updateAirCushions() 
    {
        for (AirCushion airCushion : airCushions)
            airCushion.update();
    }

    private void updatePinRopes() 
    {
        for (PinRope pinRope : pinRopes)
            pinRope.update();
    }

    private void updateStars() 
    {
        for (Star star : stars) 
            star.update();
    }
    
    public void drawDebug(Graphics2D g) 
    {
        if (pet != null)
            pet.drawDebug(g);
        if (candy != null && candy.isVisible())
            candy.drawDebug(g);
        for (Rope rope : ropes)
            rope.drawDebug(g);
        for (Pin pin : pins)
            pin.drawDebug(g);
        for (Spikes spike : spikesList) 
            spike.drawDebug(g);
        for (AirCushion airCushion : airCushions) 
            airCushion.drawDebug(g);
        for (PinRope pinRope : pinRopes) 
            pinRope.drawDebug(g);
        for (Star star : stars) 
        {
            if (star.isVisible()) 
                star.drawDebug(g);
        }
        for (Bubble bubble : bubbles) 
        {
            if (bubble.isVisible()) 
                bubble.drawDebug(g);
        }
        slashTrail.drawDebug(g);
    }
   
    public void startLevel(String levelName) 
    {
        clear();
        levelLoader.loadFromResource(levelName);
        currentLevelName = levelName;
        System.gc();
    }
    
    public void retryCurrentLevel() 
    {
        if(currentLevelName != null) 
            startLevel(currentLevelName);
    }
    
    void levelFailured() 
    {
        if(levelFailured) 
            return;
        levelFailured = true;
        fireOnFailured();
    }
    
    void levelCleared() 
    {
        levelCleared = true;
        candy.setVisible(false);
        fireOnLevelCleared();
    }
    
    private void fireOnFailured() 
    {
        for(ModelListener listener : listeners) 
            listener.onFailure();
    }

    private void fireOnLevelCleared() 
    {
        for(ModelListener listener : listeners) 
            listener.onLevelCleared();
    }
}
