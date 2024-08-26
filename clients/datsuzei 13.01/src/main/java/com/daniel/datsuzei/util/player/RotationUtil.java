package com.daniel.datsuzei.util.player;

import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import com.daniel.datsuzei.util.math.MathUtil;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil implements MinecraftClient {

    public static float rotationYaw, rotationPitch, lastRotationYaw, lastRotationPitch;
    public static boolean movementAngleCorrection;

    public static float adjustYaw(float targetYaw, float yaw) {
        return yaw - yaw % 360 + targetYaw % 360;
    }

    // Gets the closest point on an entity
    public static Vec3 getBestLookVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathUtil.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathUtil.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathUtil.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public static float[] getRotation(Vec3 aimVector) {
        double x = aimVector.xCoord - mc.thePlayer.posX;
        double y = aimVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = aimVector.zCoord - mc.thePlayer.posZ;

        double d3 = Math.sqrt(x * x + z * z);
        float f = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(Math.atan2(y, d3) * (180 / Math.PI)));
        f1 = MathUtil.clamp(f1, -90, 90);
        return new float[]{f, f1};
    }

    public static float[] applyMouseSensitivity(float[] rotation) {
        // Fix situation with 0 sensitivity
        final float sensitivity = Math.max(0.001F, mc.gameSettings.mouseSensitivity);

        // Code from Minecraft's mouse sensitivity implementation
        final int deltaYaw = (int) ((rotation[0] - rotationYaw) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2)));
        final int deltaPitch = (int) ((rotation[1] - rotationPitch) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2))) * -1;
        final float f = sensitivity * 0.6F + 0.2F;
        final float f1 = f * f * f * 8.0F;
        final float f2 = (float) deltaYaw * f1;
        final float f3 = (float) deltaPitch * f1;

        // Adding the delta to the current yaw and pitch
        final float endYaw = (float) ((double) rotationYaw + (double) f2 * 0.15);
        float endPitch = (float) ((double) rotationPitch - (double) f3 * 0.15);

        // Clamping the pitch to avoid an illegal rotations
        endPitch = MathUtil.clamp(endPitch, -90, 90);

        // Return the final rotation
        return new float[]{endYaw, endPitch};
    }

    public static float clampDelta(float current, float next, float maximum) {
        float f = MathHelper.wrapAngleTo180_float(next - current);

        if (f > (float) maximum) {
            f = (float) maximum;
        }

        if (f < -(float) maximum) {
            f = -(float) maximum;
        }

        return current + f;
    }


}
