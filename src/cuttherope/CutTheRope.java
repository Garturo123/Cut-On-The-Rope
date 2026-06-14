package cuttherope;
import ctr.View;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class CutTheRope 
{
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable()
            {
              @Override  
              public void run()
              {
                View view = new View();
                JFrame frame = new JFrame();
                frame.setTitle("Cut The Rope");
                frame.getContentPane().add(view);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setVisible(true);
                view.requestFocus();
                view.start();
              }
            }
        );
    }
}
