package lord.daniel.alexander.util.player;

import lombok.Getter;
import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class MoveUtil implements Methods {

    @Getter
    private final static MoveUtil moveUtil = new MoveUtil();

    public float getDirection(float rotationYaw) {
        float left = Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed ? mc.gameSettings.keyBindBack.pressed ? 45 : mc.gameSettings.keyBindForward.pressed ? -45 : -90 : 0;
        float right = Minecraft.getMinecraft().gameSettings.keyBindRight.pressed ? mc.gameSettings.keyBindBack.pressed ? -45 : mc.gameSettings.keyBindForward.pressed ? 45 : 90 : 0;
        float back = Minecraft.getMinecraft().gameSettings.keyBindBack.pressed ? + 180 : 0;
        float yaw = left + right + back;
        return rotationYaw + yaw;
    }

    public double getSpeed(EntityPlayer player) {
        return Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
    }

    public double[] getSpeed(double speed, float yaw, boolean direction) {
        final double motionX = -Math.sin(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        final double motionZ = Math.cos(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        return new double[] {motionX, motionZ};
    }

    public void setSpeed(double speed) {
        this.setSpeed(speed, getPlayer().rotationYaw);
    }

    public void setSpeed(double speed, float yaw) {
        this.setSpeed(speed, yaw, true);
    }

    public void setSpeed(double speed, float yaw, boolean direction) {
        getPlayer().motionX = -Math.sin(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        getPlayer().motionZ = Math.cos(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
    }

    public void strafe() {
        setSpeed(getSpeed(mc.thePlayer));
    }

    public float getBaseSpeed() {
        float baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873f;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int ampl = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * ampl);
        }
        return baseSpeed;
    }

    public float getSpeedBoost(float times) {
        float boost = (getBaseSpeed() - 0.2875F) * times;
        if(0 > boost) {
            boost = 0;
        }

        return boost;
    }

    public double[] getMotion(final double speed, final float strafe, final float forward, final float yaw) {
        final float friction = (float)speed;
        final float f1 = MathHelper.sin(yaw * (float)Math.PI / 180.0f);
        final float f2 = MathHelper.cos(yaw * (float)Math.PI / 180.0f);
        final double motionX = strafe * friction * f2 - forward * friction * f1;
        final double motionZ = forward * friction * f2 + strafe * friction * f1;
        return new double[] { motionX, motionZ };
    }

    public double roundToGround(final double posY) {
        return Math.round(posY / 0.015625) * 0.015625;
    }

}
