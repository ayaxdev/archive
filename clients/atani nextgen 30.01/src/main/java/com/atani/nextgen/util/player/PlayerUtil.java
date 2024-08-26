package com.atani.nextgen.util.player;

import com.atani.nextgen.util.minecraft.MinecraftClient;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;

@UtilityClass
public class PlayerUtil implements MinecraftClient {

    public double getLookRangeToEntity(Entity entity) {
        if (mc.thePlayer == null || entity == null)
            return 0;

        return mc.thePlayer.getPositionEyes(1.0f).distanceTo(
                RotationUtil.getBestLookVector(mc.thePlayer.getPositionEyes(1F),
                        entity.getEntityBoundingBox()));
    }

}
