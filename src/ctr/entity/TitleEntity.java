package ctr.entity;
import ctr.Entity;
import ctr.FontRenderer;
import ctr.Mouse;
import ctr.Scene;
import ctr.Scene.GameState;
import static ctr.Scene.GameState.*;
import ctr.View;
import ctr.ui.Button;
import ctr.ui.ButtonListener;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TitleEntity extends Entity 
{
    private FadeEffectEntity fadeEffect;
    private CurtainEntity curtain;
    private BufferedImage title;
    private BufferedImage titleShadow;
    private AffineTransform titleShadowTransform = new AffineTransform();
    private double titleShadowAngle = Math.toRadians(45);
    private Button button;
    private ButtonListener buttonListener;
    private boolean gameStarted;

    public TitleEntity(Scene scene, FadeEffectEntity fadeEffect, CurtainEntity curtain) 
    {
        super(scene);
        title = loadImageFromResource("/res/title.png");
        titleShadow = loadImageFromResource("/res/title_shadow.png");
        loadImageFromResource("/res/title_background.png");
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.fadeEffect = fadeEffect;
        this.curtain = curtain;
        button = new Button(scene, "Play", 60, 42, 315, 370);
        buttonListener = new ButtonListener() 
        {
            @Override
            public void onClick() 
            {
                gameStarted = true;
            }
        };
    }


    
    @Override
    protected void updateTitle()
    {
        titleShadowAngle += 0.0025;
        switch (instructionPointer) 
        {
            case 0:
                setCurrentWaitTime();
                instructionPointer = 1;
            case 1:
                if(!checkPassedTime(1))
                    return;
                fadeEffect.fadeIn();
                instructionPointer = 2;
            case 2:
                button.setListener(buttonListener);
                instructionPointer = 3;
            case 3:
                if(gameStarted)
                {
                    curtain.close();
                    instructionPointer = 4;
                }
                return;
            case 4:
                if(!curtain.isFinished())
                    return;
                setCurrentWaitTime();
                instructionPointer = 5;
            case 5:
                if(!checkPassedTime(0.1))
                    return;
                scene.startLevel(1);
        }
    }
    
    @Override
    public void draw(Graphics2D g) 
    {
        g.drawImage(image, 0, 0, null);
        titleShadowTransform.setToIdentity();
        titleShadowTransform.translate(View.SCREEN_WIDTH / 2, View.SCREEN_HEIGHT / 2);
        titleShadowTransform.rotate(titleShadowAngle);
        titleShadowTransform.translate(-titleShadow.getWidth() / 2, -titleShadow.getHeight() / 2);
        g.drawImage(titleShadow, titleShadowTransform, null);
        g.drawImage(title, 180, 150, null);
        if(button.isVisible())
            button.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) 
    {
        visible = false;
        if(newGameState == TITLE) 
        {
            visible = true;
            instructionPointer = 0;
            button.setListener(null);
            button.reset();
            gameStarted = false;
        }
    }
}
