package ctr.model;
import ctr.ResourceLoader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevelLoader 
{
    private Model model;
    private Map<String, Object> objects = new HashMap<String, Object>();
    
    public LevelLoader(Model model) {   this.model = model; }

    public void loadFromResource(String levelName) 
    {
        objects.clear();
        try 
        {
            load(levelName);
        } catch (Exception ex) 
        {
            Logger.getLogger(LevelLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    private void load(String levelName) throws Exception 
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(ResourceLoader.open(levelName), "UTF-8"));
        String line = null;
        try {
        while((line = br.readLine()) != null) 
        {
            line = line.trim();
            if(line.isEmpty() || line.startsWith("#"))
                continue;
            String[] args = line.split(",");
            String cmd = args[0].trim();
            String name = args[1].trim();
            //AirCushion
            if (cmd.equals("ac")) 
            {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int direction = Integer.parseInt(args[4].trim());
                AirCushion airCushion = model.createAirCushion(x, y, direction);
                objects.put(name, airCushion);
            }
            //Bubble
            else if (cmd.equals("bu")) 
            {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int radius = Integer.parseInt(args[4].trim());
                Bubble bubble = model.createBubble(x, y, radius);
                objects.put(name, bubble);
            }
            //Candy
            else if (cmd.equals("ca")) {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int radius = Integer.parseInt(args[4].trim());
                Candy candy = model.createCandy(x, y, radius);
                objects.put(name, candy);
            }
            //CandyToRope
            else if (cmd.equals("cr")) 
            {
                String ropeName = args[1].trim();
                int candyPositionIndex = Integer.parseInt(args[2].trim());
                Rope rope = (Rope) objects.get(ropeName);
                rope.attach(model.getCandy(), candyPositionIndex);
            }
            //Pet
            else if (cmd.equals("pe")) 
            {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int radius = Integer.parseInt(args[4].trim());
                int closeDistance = Integer.parseInt(args[5].trim());
                Pet pet = model.createPet(x, y, radius, closeDistance);
                objects.put(name, pet);
            }
            //Pin
            else if (cmd.equals("pi")) 
            {
                String ropeName = args[2].trim();
                Rope rope = (Rope) objects.get(ropeName);
                Pin pin = model.createPin(rope.getFirstParticle());
                objects.put(name, pin);
            }
            //PinRope
            else if (cmd.equals("pr")) 
            {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int radius = Integer.parseInt(args[4].trim());
                int ropeLength = Integer.parseInt(args[5].trim());
                PinRope pinRope = model.createPinRope(x, y, radius, ropeLength);
                objects.put(name, pinRope);
            }
            //Rope
            else if (cmd.equals("ro")) 
            {
                int x1 = Integer.parseInt(args[2].trim());
                int y1 = Integer.parseInt(args[3].trim());
                int x2 = Integer.parseInt(args[4].trim());
                int y2 = Integer.parseInt(args[5].trim());
                Rope rope = model.createRope(x1, y1, x2, y2);
                objects.put(name, rope);
            }
            //Spikes
            else if (cmd.equals("sp")) 
            {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int w = Integer.parseInt(args[4].trim());
                int h = Integer.parseInt(args[5].trim());
                Spikes spikes = model.createSpikes(x, y, w, h);
                objects.put(name, spikes);
            }
            //Star
            else if (cmd.equals("st")) 
            {
                int x = Integer.parseInt(args[2].trim());
                int y = Integer.parseInt(args[3].trim());
                int radius = Integer.parseInt(args[4].trim());
                Star star = model.createStar(x, y, radius);
                objects.put(name, star);
            }
        }
        } finally {
            br.close();
        }
    }        
    
}
