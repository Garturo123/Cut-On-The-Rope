package ctr.model;

import ctr.physics.Line;
import ctr.physics.Particle;
import ctr.physics.Stick;
import ctr.physics.Vec2;
import ctr.physics.World;
import java.awt.Color;
import java.awt.Graphics2D;

public class Rope 
{
    public static final double ELASTICITY = 0.1;
    private final Model model;
    private final Vec2 a = new Vec2();
    private final Vec2 b = new Vec2();
    private final int segmentsNumber;
    private Particle[] particles;
    private Stick[] sticks;
    private static Line cutLineTmp = new Line(0, 0, 0, 0);
    private boolean cut = false;
    private long cutTime;
    
    public Rope(Model model, double x1, double y1, double x2, double y2, int segmentsNumber) 
    {
        this.model = model;
        a.set(x1, y1);
        b.set(x2, y2);
        this.segmentsNumber = segmentsNumber;
        create();
    }

    public Model getModel() {   return model;   }

    public Particle getFirstParticle()  {   return particles[0];    }

    public Particle getLastParticle()   {   return particles[particles.length - 1]; }

    public int getSegmentsNumber()  {   return segmentsNumber;  }

    public Particle[] getParticles()    {   return particles;   }

    public Stick[] getSticks()  {   return sticks;  }

    public boolean isCut()  {   return cut; }

    public long getCutTime()    {   return cutTime; }
    
    private void create() 
    {
        World world = model.getWorld();
        Vec2 vTmp = new Vec2();
        Vec2 vTmp2 = new Vec2();
        vTmp.set(b);
        vTmp.sub(a);
        double ropeSize = vTmp.getSize();
        double ropeSegmentSize = ropeSize / segmentsNumber;
        vTmp.normalize();
        vTmp.scale(ropeSegmentSize);
        particles = new Particle[segmentsNumber + 1];
        vTmp2.set(a);
        for(int p = 0; p < particles.length; p++) 
        {
            world.addParticle(particles[p] = new Particle(world, vTmp2.x, vTmp2.y));
            vTmp2.add(vTmp);
        }
        sticks = new Stick[segmentsNumber + 1];
        for(int s = 0; s < particles.length - 1; s++)
            world.addStick(sticks[s] = new Stick(particles[s], particles[s+1], ELASTICITY, true));
    }
    
    public void attach(Candy candy, int positionIndex) 
    {
        model.getWorld().addStick(sticks[sticks.length - 1] = new Stick(getLastParticle(), candy.getPoints()[positionIndex], ELASTICITY, true));
        sticks[sticks.length - 1].setSize(sticks[0].getSize());
        candy.getAttachedRopes().add(this);
    }

    public void dettachCandy() 
    {
        World world = model.getWorld();
        Particle cb = sticks[sticks.length - 1].getB();
        Particle dp = new Particle(world, cb.position.x, cb.position.y);
        sticks[sticks.length - 1].setB(dp);
        world.addParticle(dp);
        model.getCandy().getAttachedRopes().remove(this);
    }

    public void cut(Line line)  {   cut(line.getA().x, line.getA().y, line.getB().x, line.getB().y);    }
    
    public void cut(double x1, double y1, double x2, double y2) 
    {
        if(!model.isPlaying() || cut)
            return;
        World world = model.getWorld();
        cutLineTmp.getA().set(x1, y1);
        cutLineTmp.getB().set(x2, y2);
        for(Stick stick : sticks) 
        {
            Vec2 ip = cutLineTmp.getSegIntersectionPoint(stick.getLine());
            if(ip != null) 
            {
                Particle previousB = stick.getB();
                Particle np1 = new Particle(world, ip.x, ip.y);
                world.addParticle(np1);
                stick.setB(np1);
                Particle np2 = new Particle(world, ip.x, ip.y);
                world.addParticle(np2);
                world.addStick(new Stick(np2, previousB, ELASTICITY, true));
                dettachCandy();
                cut = true;
                cutTime = System.currentTimeMillis();
                break;
            }
        }
    }
    
    public void drawDebug(Graphics2D g) 
    {
        g.setColor(Color.ORANGE);
        for (Stick stick : sticks) {
            if (stick != null && stick.isVisible())
                stick.drawDebug(g);
        }
    }
}