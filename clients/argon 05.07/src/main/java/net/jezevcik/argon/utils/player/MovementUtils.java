package net.jezevcik.argon.utils.player;

import net.jezevcik.argon.event.impl.MovementEvent;
import net.jezevcik.argon.mixin.KeybindingAccessor;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.keyboard.KeyboardUtils;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;

/**
 * A set of methods used for modifying the player's movement.
 */
public class MovementUtils implements Minecraft {

    /**
     * Sets the X velocity of the player
     *
     * @param motionX The X motion
     */
    public static void setMotionX(double motionX) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        client.player.setVelocity(motionX, getMotionY(), getMotionZ());
    }

    /**
     * Sets the Y velocity of the player
     *
     * @param motionY The Y motion
     */
    public static void setMotionY(double motionY) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        client.player.setVelocity(getMotionX(), motionY, getMotionZ());
    }

    /**
     * Sets the Z velocity of the player
     *
     * @param motionZ The Z motion
     */
    public static void setMotionZ(double motionZ) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        client.player.setVelocity(getMotionX(), getMotionY(), motionZ);
    }

    /**
     * Returns the X velocity of the player
     *
     * @return The X motion
     */
    public static double getMotionX() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        return client.player.getVelocity().x;
    }

    /**
     * Returns the Y velocity of the player
     *
     * @return The Y motion
     */
    public static double getMotionY() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        return client.player.getVelocity().y;
    }

    /**
     * Returns the Z velocity of the player
     *
     * @return The Z motion
     */
    public static double getMotionZ() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        return client.player.getVelocity().z;
    }

    /**
     * @return Whether the current movement is non-zero
     */
    public static boolean isMoving() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        return client.player.forwardSpeed != 0 || client.player.sidewaysSpeed != 0;
    }

    /**
     * @return Whether any movement input is being registered
     */
    public static boolean isMovingInput(boolean raw) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        final KeyBinding[] movementInput = new KeyBinding[]{
                client.options.forwardKey, client.options.backKey,
                client.options.leftKey, client.options.rightKey,
                client.options.jumpKey, client.options.sneakKey
        };

        for (final KeyBinding keyBinding : movementInput) {
            final KeybindingAccessor accessor = ((KeybindingAccessor) keyBinding);
            if (raw && KeyboardUtils.isKeyDown(accessor.getBoundKey().getCode()))
                return true;
            else if (!raw && accessor.isPressedA())
                return true;
        }

        return false;
    }

    /**
     * Returns the player's speed.
     *
     * @return The player's speed.
     */
    public static double getSpeed() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        return Math.getSpeed(client.player.getVelocity().x, client.player.getVelocity().z);
    }

    /**
     * Sets the player's speed.
     *
     * @param speed The desired speed.
     */
    public static void setSpeed(double speed) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        final double yaw = Math.getStrafeYaw(client.player.getYaw()
                , client.player.forwardSpeed
                , client.player.sidewaysSpeed);
        final double[] motion = Math.forward(yaw);

        client.player.setVelocity(motion[0] * speed, client.player.getVelocity().y, motion[1] * speed);
    }

    /**
     * Makes the player strafe in the air.
     */
    public static void strafe() {
        setSpeed(getSpeed());
    }

    /**
     * Sets the player's speed to a movement event.
     *
     * @param speed The desired speed.
     */
    public static void setSpeed(final MovementEvent movementEvent, final double speed) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;
        assert client.world != null;

        final double yaw = Math.getStrafeYaw(client.player.getYaw()
                , client.player.forwardSpeed
                , client.player.sidewaysSpeed);
        final double[] motion = Math.forward(yaw);

        movementEvent.velocityX = motion[0] * speed;
        movementEvent.velocityZ = motion[1] * speed;
    }

    /**
     * Makes the player strafe in the air.
     */
    public static void strafe(final MovementEvent movementEvent) {
        setSpeed(movementEvent, getSpeed());
    }

    public static class Math {

        /**
         * Calculates the ideal yaw for strafing. For example, if we press A, the yaw will subtract 90.
         *
         * @return Perfect strafe yaw.
         */
        public static double getStrafeYaw(final float baseYaw, final float forward, final float sideways) {
            // Start with our current angle
            float yaw = baseYaw;

            // If we're moving backwards, reverse our yaw
            if (forward < 0f) yaw -= 180f;

            // This is for handling forward and sideways movement at the same time
            float forwardMultiplier = forward * 0.5f;
            if (forwardMultiplier == 0f) forwardMultiplier = 1f;

            if (sideways > 0f)
                yaw -= 90f * forwardMultiplier;
            else if (sideways < 0f)
                yaw += 90f * forwardMultiplier;

            return java.lang.Math.toRadians(yaw);
        }

        /**
         * Returns the velocity necessary to go forward.
         *
         * @param yaw Movement direction.
         * @return The X and Z velocity necessary to go forward.
         */
        public static double[] forward(final double yaw) {
            return new double[] {
                    -java.lang.Math.sin(yaw),
                    java.lang.Math.cos(yaw)
            };
        }

        /**
         * Return the speed value based on the X and Y velocity.
         *
         * @param velocityX X velocity.
         * @param velocityZ Y velocity.
         * @return The speed.;
         */
        public static double getSpeed(final double velocityX, final double velocityZ) {
            return java.lang.Math.pow(velocityX, 2) + java.lang.Math.pow(velocityZ, 2);
        }

        /**
         * Updates rotations based on the tick delta
         *
         * @param tickDelta The tick delta
         * @param prev Previous rotation
         * @param next Next rotation
         * @return Updated rotation
         */
        public static float handlePartialRotation(float tickDelta, float prev, float next) {
            return tickDelta == 1.0F ? next : MathHelper.lerp(tickDelta, prev, next);
        }

        /**
         * @param a The first angle
         * @param b The second angle
         * @return The distance between the two angles
         */
        public static float getAngleDifference(float a, float b) {
            return MathHelper.wrapDegrees(a - b);
        }

        /**
         * Rotates to the provided location
         *
         * @param x The X coordinate of the location
         * @param y The Y coordinate of the location
         * @param z The Z coordinate of the location
         * @return The final rotation
         */
        public static float[] rotateTo(double x, double y, double z) {
            final double targetX = x - client.player.getX()
                    , targetY = y - client.player.getEyeY()
                    , targetZ = z - client.player.getZ();
            double d3 = java.lang.Math.sqrt(targetX * targetX + targetZ * targetZ);
            float f = (float) (MathHelper.atan2(targetZ, targetX) * (180D / java.lang.Math.PI)) - 90.0F;
            float f1 = (float) (-(MathHelper.atan2(targetY, d3) * (180D / java.lang.Math.PI)));
            return new float[] {f, f1};
        }

    }    

}
