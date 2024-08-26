package com.skidding.atlas.util.minecraft.player;

import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.RotationProcessor;
import com.skidding.atlas.util.minecraft.IMinecraft;
import de.florianmichael.rclasses.math.Arithmetics;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class RotationUtil implements IMinecraft {
    public static final RotationUtil INSTANCE = new RotationUtil();

    private static final RotationProcessor rotationProcessor = ProcessorManager.getSingleton().getByClass(RotationProcessor.class);

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

    // Applies the mouse sensitivity
    public float[] applyMinecraftSensitivity(float yaw, float pitch, boolean a3) {
        float sensitivity = mc.gameSettings.mouseSensitivity;
        if(sensitivity == 0) {
            sensitivity = 0.0070422534F; //1% Sensitivity <- to fix 0.0 sensitivity
        }
        sensitivity = Math.max(0.1F, sensitivity);
        int deltaYaw = (int) ((yaw - rotationProcessor.getRotationYaw()) / (sensitivity / 2));
        int deltaPitch = (int) ((pitch - rotationProcessor.getRotationPitch()) / (sensitivity / 2)) * -1;

        if (a3) {
            deltaYaw -= (int) (deltaYaw % 0.5 + 0.25);
            deltaPitch -= (int) (deltaPitch % 0.5 + 0.25);
        }
        float f = sensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8F;
        float f2 = (float) deltaYaw * f1;
        float f3 = (float) deltaPitch * f1;

        float endYaw = (float) ((double)rotationProcessor.getRotationYaw() + (double)f2 * 0.15);
        float endPitch = (float) ((double)rotationProcessor.getRotationPitch() - (double)f3 * 0.15);
        return new float[] {endYaw, endPitch};
    }

    public static double calculateAngleToEntity(Entity entity) {
        Vec3 playerPos = new Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
        Vec3 targetPos = new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 playerLookVec = Minecraft.getMinecraft().thePlayer.getLook(1.0f);
        Vec3 playerToTarget = targetPos.subtract(playerPos).normalize();

        double dotProduct = playerToTarget.dotProduct(playerLookVec);
        return Math.toDegrees(Math.acos(dotProduct));
    }

    public void resetRotations(float yaw, float pitch, boolean silent) {
        if(silent) {
            getPlayer().rotationYaw = yaw - yaw % 360 + getPlayer().rotationYaw % 360;
        } else {
            getPlayer().rotationYaw = yaw;
            getPlayer().rotationPitch = pitch;
        }
    }

}
