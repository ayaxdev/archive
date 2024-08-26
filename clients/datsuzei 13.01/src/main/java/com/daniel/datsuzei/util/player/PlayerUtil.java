package com.daniel.datsuzei.util.player;

import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import net.minecraft.entity.Entity;

public class PlayerUtil implements MinecraftClient {

    public static boolean sprintResetOnNextTick;

    public static double getLookRangeToEntity(Entity entity) {
        if(mc.thePlayer == null || entity == null)
            return 0;

        return mc.thePlayer.getPositionEyes(1.0f).distanceTo(
                RotationUtil.getBestLookVector(mc.thePlayer.getPositionEyes(1F),
                entity.getEntityBoundingBox()));
    }

}
