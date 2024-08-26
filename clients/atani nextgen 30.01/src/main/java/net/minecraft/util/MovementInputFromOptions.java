package net.minecraft.util;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.event.impl.MovementInputEvent;
import com.atani.nextgen.util.minecraft.MinecraftClient;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput implements MinecraftClient
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            --this.moveStrafe;
        }


        MovementInputEvent movementInputEvent = new MovementInputEvent(moveForward, moveStrafe, mc.thePlayer.rotationYaw, mc.thePlayer.rotationYaw, false, false, this.gameSettings.keyBindSneak.isKeyDown());
        AtaniClient.getInstance().eventPubSub.publish(movementInputEvent);

        moveForward = movementInputEvent.moveForward;
        moveStrafe = movementInputEvent.moveStrafe;

        if(movementInputEvent.movementFix && (moveForward != 0 || moveStrafe != 0)) {
            getCorrectedMovement(moveForward, moveStrafe, movementInputEvent.rotationYaw, movementInputEvent.changeYaw, movementInputEvent.fixYaw);
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = movementInputEvent.sneak;

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }

    public void getCorrectedMovement(float forward, float strafe, float yaw, float shouldYaw, boolean fixYaw) {
        // Get the player's current yaw rotation
        float y = mc.thePlayer.rotationYaw;

        // Check if yaw correction is enabled, if so, use the specified yaw
        if (fixYaw)
            y = shouldYaw;

        // If yaw correction is enabled and the yaw is already correct, return early
        if (fixYaw && yaw == y) return;

        // Coefficient for movement on ground
        float f4 = 0.91F;

        // Adjust coefficient if the player is on the ground
        if (mc.thePlayer.onGround) {
            // Calculate slipperiness of the block below the player
            f4 = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX),
                    MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1,
                    MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91F;
        }

        // Calculate movement factors based on player's state (on ground or in the air)
        float f5;
        if (mc.thePlayer.onGround) {
            f5 = mc.thePlayer.getAIMoveSpeed() * (0.16277136F / (f4 * f4 * f4));
        } else {
            f5 = mc.thePlayer.jumpMovementFactor;
        }

        // Calculate the movement speed factor
        float f = strafe * strafe + forward * forward;
        f = f5 / f;

        // Calculate adjusted strafe and forward values based on the movement speed factor
        float fStrafe = strafe * f;
        float fForward = forward * f;

        // Calculate motion components based on yaw rotation
        float realYawSin = MathHelper.sin(y * (float) Math.PI / 180.0F);
        float realYawCos = MathHelper.cos(y * (float) Math.PI / 180.0F);
        float realYawMotionX = fStrafe * realYawCos - fForward * realYawSin;
        float realYawMotionZ = fForward * realYawCos + fStrafe * realYawSin;

        // Calculate motion components based on specified yaw
        float rotationYawSin = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
        float rotationYawCos = MathHelper.cos(yaw * (float) Math.PI / 180.0F);

        // Initialize an array to store the closest distance and corresponding movement directions
        float[] closest = new float[]{Float.NaN, 0, 0};

        // Iterate over possible strafe and forward values to find the closest match
        for (int possibleStrafe = -1; possibleStrafe <= 1; possibleStrafe++) {
            for (int possibleForward = -1; possibleForward <= 1; possibleForward++) {
                // Calculate test motion components based on specified yaw
                float testFStrafe = possibleStrafe * f;
                float testFForward = possibleForward * f;
                float testYawMotionX = testFStrafe * rotationYawCos - testFForward * rotationYawSin;
                float testYawMotionZ = testFForward * rotationYawCos + testFStrafe * rotationYawSin;

                // Calculate the difference in motion components
                float diffX = realYawMotionX - testYawMotionX;
                float diffZ = realYawMotionZ - testYawMotionZ;

                // Calculate the distance between the real and test motions
                float distance = MathHelper.sqrt_float(diffX * diffX + diffZ * diffZ);

                // Update closest values if the current distance is smaller
                if (Float.isNaN(closest[0]) || distance < closest[0]) {
                    closest[0] = distance;
                    closest[1] = possibleForward;
                    closest[2] = possibleStrafe;
                }
            }
        }

        // Update moveForward and moveStrafe with the closest movement directions
        moveForward = closest[1];
        moveStrafe = closest[2];
    }
}
