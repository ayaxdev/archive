package com.skidding.atlas.util.minecraft;

import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.RotationProcessor;

public interface IPlayer extends IMinecraft {

    default double getPosX() {
        return mc.thePlayer.posX;
    }

    default double getPosY() {
        return mc.thePlayer.posY;
    }

    default double getPosZ() {
        return mc.thePlayer.posZ;
    }

    default float getRotationYaw() {
        return ProcessorManager.getSingleton().getByClass(RotationProcessor.class).getRotationYaw();
    }

    default float getRotationPitch() {
        return ProcessorManager.getSingleton().getByClass(RotationProcessor.class).getRotationPitch();
    }

    default int getHurtTime() {
        return getPlayer().hurtTime;
    }

}