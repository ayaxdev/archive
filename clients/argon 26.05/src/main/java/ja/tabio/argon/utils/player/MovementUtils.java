package ja.tabio.argon.utils.player;

import de.florianmichael.rclasses.math.Arithmetics;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.mixin.KeybindingAccessor;
import ja.tabio.argon.utils.input.KeyboardUtils;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtils implements Minecraft {

    /**
     * Sets the X velocity of the player
     *
     * @param motionX The X motion
     */
    public static void setMotionX(double motionX) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert mc.player != null;
        assert mc.world != null;

        mc.player.setVelocity(motionX, getMotionY(), getMotionZ());
    }

    /**
     * Sets the Y velocity of the player
     *
     * @param motionY The Y motion
     */
    public static void setMotionY(double motionY) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert mc.player != null;
        assert mc.world != null;

        mc.player.setVelocity(getMotionX(), motionY, getMotionZ());
    }

    /**
     * Sets the Z velocity of the player
     *
     * @param motionZ The Z motion
     */
    public static void setMotionZ(double motionZ) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert mc.player != null;
        assert mc.world != null;

        mc.player.setVelocity(getMotionX(), getMotionY(), motionZ);
    }

    /**
     * Returns the X velocity of the player
     *
     * @return The X motion
     */
    public static double getMotionX() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert mc.player != null;
        assert mc.world != null;

        return mc.player.getVelocity().x;
    }

    /**
     * Returns the Y velocity of the player
     *
     * @return The Y motion
     */
    public static double getMotionY() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert mc.player != null;
        assert mc.world != null;

        return mc.player.getVelocity().y;
    }

    /**
     * Returns the Z velocity of the player
     *
     * @return The Z motion
     */
    public static double getMotionZ() {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert mc.player != null;
        assert mc.world != null;

        return mc.player.getVelocity().z;
    }

    /**
     * @return Whether the current movement is non-zero
     */
    public static boolean isMoving() {
        assert mc.player != null;
        assert mc.world != null;
        
        return mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0;
    }

    /**
     * @return Whether any movement input is being registered
     */
    public static boolean isMovingInput(boolean raw) {
        assert mc.player != null;
        assert mc.world != null;
        
        final KeyBinding[] movementInput = new KeyBinding[]{
                mc.options.forwardKey, mc.options.backKey,
                mc.options.leftKey, mc.options.rightKey,
                mc.options.jumpKey, mc.options.sneakKey
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
     * Sets the motion based on a motion calculated from an inputted speed
     *
     * @param speed How fast should we strafe
     */
    public static void setSpeed(float speed) {
        assert mc.player != null;
        assert mc.world != null;
        
        final double[] motion = Math.getMotion(speed);

        final Vec3d velocity = new Vec3d(motion[0], mc.player.getVelocity().y, motion[1]);
        mc.player.setVelocity(velocity);
    }

    public static class Math implements Minecraft {

        /**
         * Returns an angle looking at a certain position
         *
         * @param aimVector The position at which the angle is supposed to look at
         * @return Angle looking at aimVector
         */
        public static float[] getAngle(Vec3d aimVector, float tickDelta) {
            assert mc.player != null;
            assert mc.world != null;
            
            double x = aimVector.x - mc.player.getX();
            double y = aimVector.y - (mc.player.getY() + (double) mc.player.getStandingEyeHeight());
            double z = aimVector.z - mc.player.getZ();

            double d3 = java.lang.Math.sqrt(x * x + z * z);
            float f = (float) (java.lang.Math.atan2(z, x) * (180 / java.lang.Math.PI)) - 90.0F;
            float f1 = (float) (-(java.lang.Math.atan2(y, d3) * (180 / java.lang.Math.PI)));
            f1 = Arithmetics.clamp(f1, -90, 90);
            return new float[]{f, f1};
        }

        /**
         * @param speed How fast should we strafe
         * @return Calculates motion based on an inputted speed
         */
        public static double[] getMotion(double speed) {
            final double direction = getRelativeDirection();

            return new double[]{
                    -java.lang.Math.sin(direction) * speed,
                    java.lang.Math.cos(direction) * speed,
            };
        }

        /**
         * @return The yaw direction relative to our movement input
         */
        public static double getRelativeDirection() {
            assert mc.player != null;
            assert mc.world != null;
            
            // get our current angle (our player yaw rotation)
            float yaw = mc.player.getYaw();

            // if we're moving backwards, reverse our yaw
            if (mc.player.forwardSpeed < 0.0f) yaw -= 180.0f;

            // this is for handling holding forward & strafing side to side at the same time
            float forward = mc.player.forwardSpeed * 0.5f;
            if (forward == 0.0f) forward = 1.0f;

            final float strafe = mc.player.sidewaysSpeed;
            if (strafe > 0.0f) {
                yaw -= 90.0f * forward;
            } else if (strafe < 0.0f) {
                yaw += 90.0f * forward;
            }

            // convert the angle to radians
            return java.lang.Math.toRadians(yaw);
        }

        /**
         * @return The speed of the player
         */
        public static double getSpeed() {
            assert mc.player != null;

            return java.lang.Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x
                    + mc.player.getVelocity().z * mc.player.getVelocity().z);
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
         * @param a The first rotation
         * @param b The second rotation
         * @return The distance between the two rotations
         */
        public static double getRotationDifference(float[] a, float[] b) {
            return java.lang.Math.hypot(java.lang.Math.abs(getAngleDifference(a[0], b[0])), java.lang.Math.abs(a[1] - b[1]));
        }

    }

    
}
