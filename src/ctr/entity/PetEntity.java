package ctr.entity;

import ctr.Animation;
import ctr.Entity;
import ctr.Scene;
import static ctr.Scene.GameState.*;
import ctr.model.ModelListener;
import ctr.model.Pet;
import ctr.model.PetListener;
import java.awt.Graphics2D;

public class PetEntity extends Entity implements PetListener, ModelListener 
{    
    private CurtainEntity curtain;   
    private Pet pet;
    private Animation animation;
    private boolean levelCleared;
    private boolean gameOver;
    
    public PetEntity(Scene scene, CurtainEntity curtain) 
    {
        super(scene);
        this.pet = scene.getModel().getPet();
        this.pet.addListener(this);
        this.animation = new Animation();
        this.curtain = curtain;
        loadImageFromResource("/res/support.png");
        scene.getModel().addListener(this);
        loadAllAnimations();
    }
    
    private void loadAllAnimations() 
    {
        int x = (int) this.pet.getPosition().x - 40;
        int y = (int) this.pet.getPosition().y - 50;
        animation.addFrames("normal", "pet_normal", 0, 18, x, y, true);
        animation.addFrames("openMouth", "pet_openMouth", 0, 3, x, y, false);
        animation.addFrames("closeMouth", "pet_closeMouth", 0, 3, x, y, false);
        animation.addFrames("chew", "pet_chew", 0, 33, x - 10, y, false);
        animation.addFrames("sad", "pet_sad", 0, 13, x - 10, y, false);
        animation.selectAnimation("normal");
    }
    
    @Override
    public void updatePlaying() 
    {
        updateAnimation();
        switch (instructionPointer) 
        {
            case 0:
                setCurrentWaitTime();
                instructionPointer = 1;
            case 1:
                if (!checkPassedTime(0.5))
                    return;
                curtain.open();
                instructionPointer = 2;
            case 2:
                if (!levelCleared && !gameOver)
                    return;
                setCurrentWaitTime();
                instructionPointer = 3;
            case 3:
                if (!checkPassedTime(gameOver ? 2.0 : 1.0))
                    return;
                curtain.close();
                instructionPointer = 4;
            case 4:
                if (!curtain.isFinished())
                    return;
                if (levelCleared)
                    scene.setState(LEVEL_CLEARED);
                else if (gameOver)
                    scene.setState(GAME_OVER);
        }
    }
    
    private void updateAnimation() 
    { 
        animation.update();
        if (animation.getCurrentAnimationName().equals("closeMouth") && animation.isFinished())
            animation.selectAnimation("normal");
    }
    
    @Override
    public void draw(Graphics2D g) 
    {
        int x = (int) (pet.getPosition().x - pet.getRadius());
        int y = (int) (pet.getPosition().y - pet.getRadius());
        g.drawImage(image, x - 37, y + 2, null);
        animation.draw(g);
    }

    @Override
    public void onCandyEaten()  {   animation.selectAnimation("chew");  }

    @Override
    public void onCandyClose()  {   animation.selectAnimation("openMouth"); }

    @Override
    public void onCandyEscaped()    {   animation.selectAnimation("closeMouth");    }

    @Override
    public void onFailure()
    {
        animation.selectAnimation("sad");
        gameOver = true;
    }

    @Override
    public void onLevelCleared()    {   levelCleared = true;    }

    @Override
    public void gameStateChanged(Scene.GameState newGameState) 
    {
        visible = false;
        if (newGameState == Scene.GameState.PLAYING) 
        {
            visible = true;
            instructionPointer = 0;
            levelCleared = false;
            gameOver = false;
        }
    }     
}