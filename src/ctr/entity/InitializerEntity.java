package ctr.entity;
import ctr.Entity;
import ctr.Scene;
import static ctr.Scene.GameState.*;
import java.awt.Color;

public class InitializerEntity extends Entity 
{
    private FadeEffectEntity fadeEffect;   
    public InitializerEntity(Scene scene, FadeEffectEntity fadeEffect) 
    {
        super(scene);
        this.fadeEffect = fadeEffect;
    }

    @Override
    protected void updateInitializing() 
    {
        switch (instructionPointer) 
        {
            case 0:
                setCurrentWaitTime();
                instructionPointer = 1;
            case 1:
                if(!checkPassedTime(3))
                    return;
                fadeEffect.setTargetColor(Color.WHITE);
                fadeEffect.fadeOut();
                instructionPointer = 2;
            case 2:
                if(!fadeEffect.fadeEffectFinished())
                    return;
                scene.setState(MENU_PRINCIPAL);
        }
    }
}