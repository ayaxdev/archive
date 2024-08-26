package net.minecraft.util;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.game.MovementInputEvent;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.module.impl.movement.CorrectMovement;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput implements Methods
{
    private final GameSettings gameSettings;
    private CorrectMovement correctMovement;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        if(correctMovement == null)
            correctMovement = ModuleStorage.getModuleStorage().getByClass(CorrectMovement.class);

        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        final MovementInputEvent movementInputEvent = new MovementInputEvent(this.gameSettings.keyBindForward.isKeyDown(), this.gameSettings.keyBindLeft.isKeyDown(), this.gameSettings.keyBindBack.isKeyDown(), this.gameSettings.keyBindRight.isKeyDown());
        Modification.INSTANCE.getBus().post(movementInputEvent);

        if(!movementInputEvent.isCancelled()) {
            if (movementInputEvent.isForward())
            {
                ++this.moveForward;
            }

            if (movementInputEvent.isBack())
            {
                --this.moveForward;
            }

            if (movementInputEvent.isLeft())
            {
                ++this.moveStrafe;
            }

            if (movementInputEvent.isRight())
            {
                --this.moveStrafe;
            }
        }

        if(correctMovement.isEnabled() && correctMovement.silent.getValue() && correctMovement.silentMode.is("InputOverride") && (moveForward != 0 || moveStrafe != 0)) {
            float[] fixed = getMovementCorrection(moveForward, moveStrafe);
            moveForward = fixed[0];
            moveStrafe = fixed[1];
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }
    }

    public float[] getMovementCorrection(float forward, float strafe) {
        // Set the player's current mouse rotation yaw
        float y = mc.thePlayer.rotationYaw;

        // If the player's mouse yaw is the same as the client yaw, return the current movement inputs
        if(PlayerHandler.yaw == y) {
            return new float[] {forward, strafe};
        }

        // Determine the slipperiness factor based on the player's position
        float slipperiness = 0.91F;
        if (mc.thePlayer.onGround) {
            slipperiness = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX),
                    MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1,
                    MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91F;
        }

        // Calculate player's movement factor based on slipperiness and on-ground status
        float moveFactor;
        if (mc.thePlayer.onGround) {
            moveFactor = mc.thePlayer.getAIMoveSpeed() * (0.16277136F / (slipperiness * slipperiness * slipperiness));
        } else {
            moveFactor = mc.thePlayer.jumpMovementFactor;
        }

        // Calculate normalized movement inputs
        float magnitude = strafe * strafe + forward * forward;
        magnitude = moveFactor / magnitude;
        float normalizedStrafe = strafe * magnitude;
        float normalizedForward = forward * magnitude;

        // Calculate motion values based on current mouse yaw
        float realYawSin = MathHelper.sin(y * (float) Math.PI / 180.0F);
        float realYawCos = MathHelper.cos(y * (float) Math.PI / 180.0F);
        float realYawMotionX = normalizedStrafe * realYawCos - normalizedForward * realYawSin;
        float realYawMotionZ = normalizedForward * realYawCos + normalizedStrafe * realYawSin;

        // Calculate motion values based on client yaw
        float rotationYawSin = MathHelper.sin(PlayerHandler.yaw * (float) Math.PI / 180.0F);
        float rotationYawCos = MathHelper.cos(PlayerHandler.yaw * (float) Math.PI / 180.0F);

        // Store the closest movement direction found through testing different combinations
        float[] closest = new float[]{Float.NaN, 0, 0};

        // bruteforce all possible strafe and forward combinations
        for (int possibleStrafe = -1; possibleStrafe <= 1; possibleStrafe++) {
            for (int possibleForward = -1; possibleForward <= 1; possibleForward++) {
                float testFStrafe = possibleStrafe * magnitude;
                float testFForward = possibleForward * magnitude;
                float testYawMotionX = testFStrafe * rotationYawCos - testFForward * rotationYawSin;
                float testYawMotionZ = testFForward * rotationYawCos + testFStrafe * rotationYawSin;

                // Calculate the distance between the real and tested motions
                float diffX = realYawMotionX - testYawMotionX;
                float diffZ = realYawMotionZ - testYawMotionZ;
                float distance = MathHelper.sqrt_float(diffX * diffX + diffZ * diffZ);

                // Update closest if the current combination is closer
                if (Float.isNaN(closest[0]) || distance < closest[0]) {
                    closest[0] = distance;
                    closest[1] = possibleForward;
                    closest[2] = possibleStrafe;
                }
            }
        }

        // Return the movement inputs corresponding to the closest direction found
        return new float[] {closest[1], closest[2]};
    }
}