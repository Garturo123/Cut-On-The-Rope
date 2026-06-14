package ctr.entity;
import ctr.Entity;
import ctr.FontRenderer;
import ctr.Scene;
import ctr.Scene.GameState;
import static ctr.Scene.GameState.LEVEL_CLEARED;
import ctr.ui.Button;
import ctr.ui.ButtonListener;
import ctr.model.Star;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class LevelClearedEntity extends Entity 
{
    private FadeEffectEntity fadeEffect;
    private CurtainEntity curtain;   
    private BufferedImage levelCleared;
    private BufferedImage starOn;
    private BufferedImage starOff;
    private boolean showStars;
    private Button buttonReplay;
    private Button buttonTitle;
    private Button buttonNext;
    private ButtonListener buttonReplayListener;
    private ButtonListener buttonTitleListener;
    private ButtonListener buttonNextListener;
    private boolean buttonReplayPressed;
    private boolean buttonTitlePressed;
    private boolean buttonNextPressed;
    private boolean progressSaved;
    
    public LevelClearedEntity(Scene scene, FadeEffectEntity fadeEffect, CurtainEntity curtain) 
    {
        super(scene);
        levelCleared = new BufferedImage(200, 63, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) levelCleared.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontRenderer.draw(g, "Level cleared!", 5, 42);
        starOn = loadImageFromResource("/res/star_result_0g.png");
        starOff = loadImageFromResource("/res/star_result_1g.png");
        this.fadeEffect = fadeEffect;
        this.curtain = curtain;
        buttonReplay = new Button(scene, "Replay", 50, 42, 135, 330);
        buttonTitle = new Button(scene, "Title", 50, 42, 315, 330);
        buttonNext = new Button(scene, "Next", 55, 42, 495, 330);
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
        buttonNextListener = new ButtonListener() 
        {
            @Override
            public void onClick() 
            {
                buttonNextPressed = true;
            }
        };
        buttonNext.setListener(buttonNextListener);
    }
    
    @Override
    protected void updateLevelCleared() 
    {
        switch (instructionPointer) 
        {
            case 0:
                if (!progressSaved) {
                    scene.completeCurrentLevel(countObtainedStars());
                    progressSaved = true;
                }
                setCurrentWaitTime();
                instructionPointer = 1;
            case 1:
                if (!checkPassedTime(1))
                    return;
                buttonReplay.setVisible(true);
                buttonTitle.setVisible(true);
                buttonNext.setVisible(true);
                showStars = true;
                instructionPointer = 2;
            case 2:
                buttonReplay.update();
                buttonTitle.update();
                buttonNext.update();
                if (buttonReplayPressed)
                    scene.replayLevel();
                else if (buttonTitlePressed)
                    instructionPointer = 3;
                else if (buttonNextPressed)
                    scene.nextLevel();
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
    
    private int countObtainedStars() 
    {
        int count = 0;
        for (Star star : scene.getModel().getStars()) 
        {
            if (!star.isVisible())
                count++;
        }
        return count;
    }
    
    @Override
    public void draw(Graphics2D g) 
    {
        if (!showStars)
            return;
        g.drawImage(levelCleared, 320, 100, null);
        int starsCount = countObtainedStars();
        g.drawImage(starsCount > 0 ? starOn : starOff, 210, 170, null);
        g.drawImage(starsCount > 1 ? starOn : starOff, 335, 170, null);
        g.drawImage(starsCount > 2 ? starOn : starOff, 460, 170, null);
        if (buttonReplay.isVisible())
            buttonReplay.draw(g);
        if (buttonTitle.isVisible())
            buttonTitle.draw(g);
        if (buttonNext.isVisible())
            buttonNext.draw(g);
    }

    @Override
    public void gameStateChanged(GameState newGameState) 
    {
        visible = false;
        if (newGameState == LEVEL_CLEARED) 
        {
            visible = true;
            instructionPointer = 0;
            showStars = false;
            buttonReplayPressed = false;
            buttonTitlePressed = false;
            buttonNextPressed = false;
            progressSaved = false;
            buttonReplay.reset();
            buttonTitle.reset();
            buttonNext.reset();
            buttonReplay.setVisible(false);
            buttonTitle.setVisible(false);
            buttonNext.setVisible(false);
        }
    }
}