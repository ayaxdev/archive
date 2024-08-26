package com.skidding.atlas.util.minecraft.player;

import com.skidding.atlas.util.minecraft.IGameSettings;
import com.skidding.atlas.util.minecraft.IMinecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class MovementUtil implements IMinecraft, IGameSettings {

    public static final MovementUtil INSTANCE = new MovementUtil();

    public void stop() {
        getPlayer().motionX = 0;
        getPlayer().motionZ = 0;
    }

    public boolean isMoving() {
        return getPlayer().moveForward != 0 || getPlayer().moveStrafing != 0;
    }

    public float getDirection(float rotationYaw) {
        float left = mc.gameSettings.keyBindLeft.pressed ? mc.gameSettings.keyBindBack.pressed ? 45 : mc.gameSettings.keyBindForward.pressed ? -45 : -90 : 0;
        float right = mc.gameSettings.keyBindRight.pressed ? mc.gameSettings.keyBindBack.pressed ? -45 : mc.gameSettings.keyBindForward.pressed ? 45 : 90 : 0;
        float back = mc.gameSettings.keyBindBack.pressed ? + 180 : 0;
        float yaw = left + right + back;
        return rotationYaw + yaw;
    }

    public double[] getMotion(final double speed, final float strafe, final float forward, final float yaw) {
        final float friction = (float) speed;
        final float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0f);
        final float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0f);
        final double motionX = strafe * friction * f2 - forward * friction * f1;
        final double motionZ = forward * friction * f2 + strafe * friction * f1;
        return new double[]{motionX, motionZ};
    }

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (getPlayer().isPotionActive(Potion.moveSpeed)) {
            int amplifier = getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }

    public void teleportTo(double speed, double y, float yaw) {
        double motionX = -Math.sin(Math.toRadians(getDirection(yaw))) * speed;
        double motionZ = Math.cos(Math.toRadians(getDirection(yaw))) * speed;
        getPlayer().setPosition(getPlayer().posX + motionX, y, getPlayer().posZ + motionZ);
    }

    public double getSpeed() {
        return getPlayer() == null ? 0 : Math.sqrt(getPlayer().motionX * getPlayer().motionX + getPlayer().motionZ * getPlayer().motionZ);
    }

    public double getBaseSpeed() {
        double baseSpeed = 0.272;
        if (getPlayer().isPotionActive(Potion.moveSpeed)) {
            final int amplifier = getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }

    public void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        getPlayer().motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        getPlayer().motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

    public void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, getPlayer().rotationYaw, getPlayer().movementInput.moveStrafe, getPlayer().movementInput.moveForward);
    }

    public boolean isBlockUnder() {
        if (getPlayer().posY < 0) {
            return false;
        }
        for (int offset = 0; offset < (int) getPlayer().posY + 2; offset += 2) {
            AxisAlignedBB bb = getPlayer().getEntityBoundingBox().offset(0, -offset, 0);
            if (!getWorld().getCollidingBoundingBoxes(getPlayer(), bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void strafe() {
        strafe(getSpeed());
    }

    public void strafe(double movementSpeed) {
        if (getPlayer().movementInput.moveForward > 0.0) {
            getPlayer().movementInput.moveForward = (float) 1.0;
        } else if (getPlayer().movementInput.moveForward < 0.0) {
            getPlayer().movementInput.moveForward = (float) -1.0;
        }

        if (getPlayer().movementInput.moveStrafe > 0.0) {
            getPlayer().movementInput.moveStrafe = (float) 1.0;
        } else if (getPlayer().movementInput.moveStrafe < 0.0) {
            getPlayer().movementInput.moveStrafe = (float) -1.0;
        }

        if (getPlayer().movementInput.moveForward == 0.0 && getPlayer().movementInput.moveStrafe == 0.0) {
            getPlayer().motionX = 0.0;
            getPlayer().motionZ = 0.0;
        }

        if (getPlayer().movementInput.moveForward != 0.0 && getPlayer().movementInput.moveStrafe != 0.0) {
            getPlayer().movementInput.moveForward *= (float) Math.sin(0.6398355709958845);
            getPlayer().movementInput.moveStrafe *= (float) Math.cos(0.6398355709958845);
        }

        getPlayer().motionX = getPlayer().movementInput.moveForward * movementSpeed * -Math.sin(Math.toRadians(getPlayer().rotationYaw)) + getPlayer().movementInput.moveStrafe * movementSpeed * Math.cos(Math.toRadians(getPlayer().rotationYaw));
        getPlayer().motionZ = getPlayer().movementInput.moveForward * movementSpeed * Math.cos(Math.toRadians(getPlayer().rotationYaw)) - getPlayer().movementInput.moveStrafe * movementSpeed * -Math.sin(Math.toRadians(getPlayer().rotationYaw));
    }

    public float getDirection(float forward, float strafing, float yaw) {
        if (forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = (forward < 0.0);
        float strafingYaw = 90f * ((forward > 0) ? 0.5f : (reversed ? -0.5f : 1));
        if (reversed) yaw += 180;
        if (strafing > 0) {
            yaw -= strafingYaw;
        } else if (strafing < 0) {
            yaw += strafingYaw;
        }
        return yaw;
    }

    public float getDirection() {
        return getDirection(getPlayer().moveForward, getPlayer().moveStrafing, getPlayer().rotationYaw);
    }

    public float getSpeedBoost(float times) {
        float boost = (float) ((getBaseMoveSpeed() - 0.2875F) * times);
        if(0 > boost) {
            boost = 0;
        }

        return boost;
    }

    public int getSpeedAmplifier() {
        return getPlayer().isPotionActive(Potion.moveSpeed) ? 1 + getPlayer().getActivePotionEffect(Potion.moveSpeed).getAmplifier() : 0;
    }

    public float getBPS() {
        return (float)getPlayer().getDistance(getPlayer().lastTickPosX, getPlayer().posY, getPlayer().lastTickPosZ) * (20 * mc.timer.timerSpeed);
    }

    public void resumeWalk() {
        mc.gameSettings.keyBindForward.pressed = isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
    }

    public void stopWalk() {
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
    }

}
