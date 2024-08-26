package com.skidding.atlas.util.minecraft.rtx;

import com.skidding.atlas.util.minecraft.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RaytraceUtil implements IMinecraft {

    public static final RaytraceUtil INSTANCE = new RaytraceUtil();


    public Vec3 getLook(float yaw, float pitch) {
        return getLook(yaw, pitch, 1F);
    }

    public Vec3 getLook(float yaw, float pitch, float partialTicks) {
        return Entity.getVectorForRotation(pitch, yaw);
    }


    public MovingObjectPosition rayTrace(float yaw, float pitch, float reach) {
        return rayTrace(getPlayer(), yaw, pitch, reach);
    }

    public MovingObjectPosition rayTrace(Entity entity, float yaw, float pitch, float reach) {
        Vec3 vec3 = entity.getPositionEyes(1F);
        Vec3 vec31 = getLook(yaw, pitch);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);
        return getWorld().rayTraceBlocks(vec3, vec32, false, false, true);
    }

}
