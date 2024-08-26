package ja.tabio.argon.utils.player;

import de.florianmichael.rclasses.math.Arithmetics;
import ja.tabio.argon.interfaces.IMinecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

public class MovementUtil implements IMinecraft {

    /**
     * @return Whether the current movement is non-zero
     */
    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    /**
     * @return Whether any movement input is being registered
     */
    public static boolean isMovingInput(boolean raw) {
        final KeyBinding[] movementInput = new KeyBinding[] {
            mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak
        };

        for (final KeyBinding keyBinding : movementInput) {
            if (raw && Keyboard.isKeyDown(keyBinding.getKeyCode()))
                return true;
            else if(!raw && keyBinding.pressed)
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
        final double[] motion = Math.getMotion(speed);

        mc.thePlayer.motionX = motion[0];
        mc.thePlayer.motionZ = motion[1];
    }

    public static class Math implements IMinecraft {

        /**
         * Returns an angle looking at a certain position
         *
         * @param aimVector The position at which the angle is supposed to look at
         * @return Angle looking at aimVector
         */
        public static float[] getAngle(Vec3 aimVector) {
            double x = aimVector.xCoord - mc.thePlayer.posX;
            double y = aimVector.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
            double z = aimVector.zCoord - mc.thePlayer.posZ;

            double d3 = java.lang.Math.sqrt(x * x + z * z);
            float f = (float) (java.lang.Math.atan2(z, x) * (180 / java.lang.Math.PI)) - 90.0F;
            float f1 = (float) (-(java.lang.Math.atan2(y, d3) * (180 / java.lang.Math.PI)));
            f1 = Arithmetics.clamp(f1, -90, 90);
            return new float[]{f, f1};
        }

        /**
         * @param speed How fast should we strafe
         *
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
            // get our current angle (our player yaw rotation)
            float yaw = mc.thePlayer.rotationYaw;

            // if we're moving backwards, reverse our yaw
            if (mc.thePlayer.moveForward < 0.0f) yaw -= 180.0f;

            // this is for handling holding forward & strafing side to side at the same time
            float forward = mc.thePlayer.moveForward * 0.5f;
            if (forward == 0.0f) forward = 1.0f;

            float strafe = mc.thePlayer.moveStrafing;
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
            return java.lang.Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX
                    + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
        }

    }

}
