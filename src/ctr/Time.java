package ctr;

public class Time
{
    private static double frameRate = 1 / 60.0;
    private static int updateCount;
    private static double delta;
    private static double previous = -1;
    private static double current;
    private static double unprocessed;

    public static double getDelta() {   return delta;   }

    public static double getCurrent()   { return current;   }

    public static boolean needsUpdate() 
    {
        if(updateCount > 0) 
        {
            updateCount--;
            return true;
        }
        return false;
    }
    
    public static void update() 
    {
        current = System.nanoTime() * 0.000000001;
        if(previous < 0)
            previous = current;
        delta = current - previous;
        if (delta > 0.25)
            delta = 0.25;
        previous = current;
        unprocessed += delta;
        while(unprocessed > frameRate) 
        {
            unprocessed -= frameRate;
            updateCount++;
        }
    }
}
