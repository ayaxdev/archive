package com.skidding.atlas.util.minecraft.player;

import com.skidding.atlas.util.minecraft.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;

@UtilityClass
public class PlayerUtil implements IMinecraft {

    public boolean shouldSprintReset;

    public double getLookRangeToEntity(Entity entity) {
        if (mc.thePlayer == null || entity == null)
            return 0;

        return mc.thePlayer.getPositionEyes(1.0f).distanceTo(
                RotationUtil.getBestLookVector(mc.thePlayer.getPositionEyes(1F),
                        entity.getEntityBoundingBox()));
    }

}
