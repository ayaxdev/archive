package lord.daniel.alexander.util.player.rotation;

import lombok.Getter;
import lord.daniel.alexander.handler.player.PlayerHandler;
import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.interfaces.IPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.AxisAlignedBB;

public class RotationUtil implements IMinecraft, IPlayer {

    @Getter
    private final static RotationUtil rotationUtil = new RotationUtil();

    public float[] applyMouseFix(float newYaw, float newPitch) {
        return applyMouseFix(newYaw, newPitch, mc.gameSettings.mouseSensitivity);
    }

    public float[] applyMouseFix(float newYaw, float newPitch, float mouseSpeed) {
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

    public float[] getFovToTarget(double posX, double posY, double posZ, float yaw, float pitch) {
        double x = posX - mc.thePlayer.posX;
        double y = posY - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double z = posZ - mc.thePlayer.posZ;
        float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / Math.PI - 90.0);
        float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / Math.PI));
        float diffY = MathHelper.wrapAngleTo180_float(calcYaw - yaw);
        float diffP = MathHelper.wrapAngleTo180_float(calcPitch - pitch);
        return new float[]{diffY, diffP};
    }

    public Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp_double(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp_double(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp_double(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    public float[] getRotation(Vec3 aimVector) {
        double x = aimVector.xCoord - mc.thePlayer.posX;
        double y = aimVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = aimVector.zCoord - mc.thePlayer.posZ;

        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float f = (float) (MathHelper.func_181159_b(z, x) * (180 / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.func_181159_b(y, d3) * (180 / Math.PI)));
        f1 = MathHelper.clamp_float(f1, -90, 90);
        return new float[]{f, f1};
    }


}
