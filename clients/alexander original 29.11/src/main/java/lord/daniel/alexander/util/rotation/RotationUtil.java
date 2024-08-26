package lord.daniel.alexander.util.rotation;

import com.google.common.base.Predicates;
import lombok.experimental.UtilityClass;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;

import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class RotationUtil implements Methods {

    public Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp_double(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp_double(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp_double(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public static float[] applyMouseFix(float newYaw, float newPitch) {
        return applyMouseFix(newYaw, newPitch, mc.gameSettings.mouseSensitivity);
    }

    public static float[] applyMouseFix(float newYaw, float newPitch, float mouseSpeed) {
        final float sensitivity = Math.max(0.001F, mouseSpeed);
        final int deltaYaw = (int) ((newYaw - PlayerHandler.yaw) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2)));
        final int deltaPitch = (int) ((newPitch - PlayerHandler.pitch) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2))) * -1;
        final float f = sensitivity * 0.6F + 0.2F;
        final float f1 = f * f * f * 8.0F;
        final float f2 = (float) deltaYaw * f1;
        final float f3 = (float) deltaPitch * f1;

        final float endYaw = (float) ((double) PlayerHandler.yaw + (double) f2 * 0.15);
        float endPitch = (float) ((double) PlayerHandler.pitch - (double) f3 * 0.15);
        endPitch = MathHelper.clamp_float(endPitch, -90, 90);
        return new float[]{endYaw, endPitch};
    }

    public float[] getRotation(Vec3 aimVector) {
        double x = aimVector.xCoord - mc.thePlayer.posX;
        double y = aimVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = aimVector.zCoord - mc.thePlayer.posZ;

        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float f = (float) (MathHelper.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180 / Math.PI)));
        f1 = MathHelper.clamp_float(f1, -90, 90);
        return new float[]{f, f1};
    }

    public float[] calculateRotationDiff(float yaw, float yaw1) {
        float y = Math.abs(yaw - yaw1);
        if (y < 0) y += 360;
        if (y >= 360) y -= 360;
        float y1 = 360 - y;
        float oneoranother = 0;
        if (y > y1) oneoranother++;
        if (y > y1) y = y1;
        return new float[]{y, oneoranother};
    }
    
    public void resetRotations(float yaw, float pitch, boolean silent) {
        if(silent) {
            mc.thePlayer.rotationYaw = yaw - yaw % 360 + mc.thePlayer.rotationYaw % 360;
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    public float getYaw(Vec3 vec) {
        double xD = (mc.thePlayer.posX) - (vec.xCoord);
        double zD = (mc.thePlayer.posZ) - (vec.zCoord);
        return (float) ((float) Math.atan2(zD, xD) / Math.PI * 180) - 90;
    }

    public float getYaw(Entity en, Entity en2) {
        Vec3 vec = new Vec3(en2.posX, en2.posY, en2.posZ);
        double xD = (en.posX) - (vec.xCoord + (en2.posX - en2.lastTickPosX));
        double zD = (en.posZ) - (vec.zCoord + (en2.posZ - en2.lastTickPosZ));
        return (float) ((float) Math.atan2(zD, xD) / Math.PI * 180) - 90;
    }

    public static float getDistanceToLastPitch(final float pitch) {
        return Math.abs(pitch - PlayerHandler.pitch);
    }

    public static Vec3 toDirection(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * 0.017453292f - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292f - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292f);
        float f3 = MathHelper.sin(-pitch * 0.017453292f);

        double x = (f1 * f2);
        double y = f3;
        double z = (f * f2);

        return new Vec3(x, y, z);
    }

    public static float[] getFovToTarget(double posX, double posY, double posZ, float yaw, float pitch) {
        double x = posX - mc.thePlayer.posX;
        double y = posY - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double z = posZ - mc.thePlayer.posZ;
        float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / Math.PI - 90.0);
        float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / Math.PI));
        float diffY = MathHelper.wrapAngleTo180_float(calcYaw - yaw);
        float diffP = MathHelper.wrapAngleTo180_float(calcPitch - pitch);
        return new float[]{diffY, diffP};
    }

    public static Vec3 toDirection(float[] rotation) {
        return RotationUtil.toDirection(rotation[0], rotation[1]);
    }

}
