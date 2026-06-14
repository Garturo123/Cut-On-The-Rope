package ctr.entity;
import ctr.Entity;
import ctr.FontRenderer;
import ctr.Scene;
import ctr.Scene.GameState;
import static ctr.Scene.GameState.*;
import ctr.ui.Button;
import ctr.ui.ButtonListener;
import ctr.model.Star;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class GameOverEntity extends Entity 
{
    private FadeEffectEntity fadeEffect;
    private CurtainEntity curtain;
    private BufferedImage levelFailed;
    private boolean showLevelFailed;
    private Button buttonReplay;
    private Button buttonTitle;
    private ButtonListener buttonReplayListener;
    private ButtonListener buttonTitleListener;
    private boolean buttonReplayPressed;
    private boolean buttonTitlePressed;
    
    public GameOverEntity(Scene scene, FadeEffectEntity fadeEffect, CurtainEntity curtain) 
    {
        super(scene);
        levelFailed = new BufferedImage(200, 63, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) levelFailed.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontRenderer.draw(g, "Level failed!", 5, 42);
        this.fadeEffect = fadeEffect;
        this.curtain = curtain;
        buttonReplay = new Button(scene, "Replay", 50, 42, 235, 330);
        buttonTitle = new Button(scene, "Title", 50, 42, 415, 330);
        buttonReplayListener = new ButtonListener() 
        {
            @Override
            public void onClick() 
            {
                buttonReplayPressed = true;
            }
        };
        buttonReplay.setListener(buttonReplayListener);
        buttonTitleListener = new ButtonListener() 
        {
            @Override
            public void onClick() 
            {
                buttonTitlePressed = true;
            }
        };
        buttonTitle.setListener(buttonTitleListener);
    }


    @Override
    protected void updateGameOver() 
    {
        switch (instructionPointer) 
        {
            case 0:
                setCurrentWaitTime();
                instructionPointer = 1;
            case 1:
                if (!checkPassedTime(1))
                    return;
                buttonReplay.setVisible(true);
                buttonTitle.setVisible(true);
                showLevelFailed = true;
                instructionPointer = 2;
            case 2:
                buttonReplay.update();
                buttonTitle.update();
                if (buttonReplayPressed)
                    scene.replayLevel();
                else if (buttonTitlePressed)
                    instructionPointer = 3;
                return;
            case 3:
                fadeEffect.setTargetColor(Color.BLACK);
                fadeEffect.fadeOut();
                instructionPointer = 4;
            case 4:
                if (!fadeEffect.fadeEffectFinished())
                    return;
                curtain.openInstantly();
                scene.backToTitle();
        }
    }
    
    @Override
    public void draw(Graphics2D g) 
    {
        if (!showLevelFailed)
            return;
        g.drawImage(levelFailed, 320, 180, null);
        if (buttonReplay.isVisible())
            buttonReplay.draw(g);
        if (buttonTitle.isVisible())
            buttonTitle.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) 
    {
        visible = false;
        if (newGameState == GAME_OVER) 
        {
            visible = true;
            instructionPointer = 0;
            showLevelFailed = false;
            buttonReplayPressed = false;
            buttonTitlePressed = false;
            buttonReplay.reset();
            buttonTitle.reset();
            buttonReplay.setVisible(false);
            buttonTitle.setVisible(false);
        }
    }
}