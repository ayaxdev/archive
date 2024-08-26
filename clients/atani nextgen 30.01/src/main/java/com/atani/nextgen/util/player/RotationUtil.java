package com.atani.nextgen.util.player;

import com.atani.nextgen.util.minecraft.MinecraftClient;
import de.florianmichael.rclasses.math.Arithmetics;
import lombok.experimental.UtilityClass;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

@UtilityClass
public class RotationUtil implements MinecraftClient {

    // Gets the closest point on an entity
    public static Vec3 getBestLookVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(Arithmetics.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), Arithmetics.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), Arithmetics.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    // Gets the rotations to an aim vector
    public static float[] getRotation(Vec3 aimVector) {
        double x = aimVector.xCoord - mc.thePlayer.posX;
        double y = aimVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = aimVector.zCoord - mc.thePlayer.posZ;

        double d3 = Math.sqrt(x * x + z * z);
        float f = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(Math.atan2(y, d3) * (180 / Math.PI)));
        f1 = Arithmetics.clamp(f1, -90, 90);
        return new float[]{f, f1};
    }

}
