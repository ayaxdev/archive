package net.jezevcik.argon.processor.impl.rotation;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.*;
import net.jezevcik.argon.processor.Processor;
import net.jezevcik.argon.processor.impl.rotation.interfaces.RotationModifier;
import net.jezevcik.argon.processor.impl.rotation.interfaces.Rotator;
import net.jezevcik.argon.processor.impl.rotation.strafe.StrafeCorrector;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.player.MovementUtils;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class RotationProcessor extends Processor {

    private final List<Rotator> rotators = new ArrayList<>();
    private Rotator activeRotator;

    private final float[] previousRotations = new float[2];
    private final float[] reportedRotations = new float[2];
    private final float[] serverRotations = new float[2];

    private boolean backRotating, strafing;
    private StrafeCorrector corrector;

    public RotationProcessor() {
        super("Rotations");
    }

    public void add(Rotator rotator) {
        this.rotators.add(rotator);
    }

    private void spoof(boolean tick, float tickDelta) {
        if (activeRotator == null || !Minecraft.inGame())
            return;

        // Checked in inGame(), only here so ide won't complain
        assert client.player != null;
        assert client.world != null;

        // If back-rotating rotate to the client rotations
        if (backRotating) {
            rotate(new float[] {
                    client.player.getYaw(),
                    client.player.getPitch()
            }, tickDelta);

            // If the new server rotations match the client rotations, back-rotation's been finished
            if(serverRotations[0] == client.player.getYaw() && serverRotations[1] == client.player.getPitch()) {
                this.activeRotator = null;
                this.backRotating = false;
            } else {
                this.backRotating = true;
            }
        } else {
            // If not, then get target rotations from the active rotator and rotate to those
            rotate(activeRotator.rotate(reportedRotations, tick, tickDelta), tickDelta);
        }
    }

    private void rotate(float[] targetRotations, float tickDelta) {
        if((activeRotator.getFlags() & RotatorFlags.DISABLE_UPDATERS) != RotatorFlags.DISABLE_UPDATERS && !Arrays.equals(targetRotations, reportedRotations)) {
            for (RotationModifier rotationModifier : activeRotator.getModifiers().values()) {
                if (rotationModifier.isEnabled()) {
                    final float[] updated = rotationModifier.modifier(reportedRotations, targetRotations, serverRotations, tickDelta, backRotating);

                    // Error correction code. Shouldn't need to run if the developer is not brain-damaged
                    if (!Float.isNaN(updated[0]) && !Float.isNaN(updated[1])) {
                        targetRotations = updated;
                    } else {
                        ParekClient.LOGGER.info("Updated rotations are NaN, this should not happen!");
                    }
                }
            }
        }

        // Prevent going out of the limit
        targetRotations[1] = MathHelper.clamp(targetRotations[1], -90F, 90F);

        if ((activeRotator.getFlags() & RotatorFlags.LOG) == RotatorFlags.LOG) {
            ParekClient.LOGGER.info("ROTATION LOG: {} {}", targetRotations[0], targetRotations[1]);
        }

        // There is no need to update the rotations if they already match
        // This is only down here instead of the head of the method as to allow randomization updaters
        if (Arrays.equals(reportedRotations, targetRotations))
            return;

        serverRotations[0] = targetRotations[0];
        serverRotations[1] = targetRotations[1];
    }

    @EventHandler
    public final void onMoveCamera(MoveCameraEvent moveCameraEvent) {
        if (!Minecraft.inGame() || client.cameraEntity == null)
            return;

        // Checked in inGame(), only here so ide won't complain
        assert client.player != null;

        if (activeRotator != null) {
            spoof(false, moveCameraEvent.tickDelta);

            if((activeRotator.getFlags() & RotatorFlags.NON_SILENT) == RotatorFlags.NON_SILENT) {
                client.player.setYaw(MathHelper.lerpAngleDegrees(moveCameraEvent.tickDelta, client.player.prevYaw, serverRotations[0]));
                client.player.setPitch(MathHelper.lerpAngleDegrees(moveCameraEvent.tickDelta, client.player.prevPitch, serverRotations[1]));
            }
        } else {
            serverRotations[0] = client.player.getYaw();
            serverRotations[1] = client.player.getPitch();
        }
    }

    @EventHandler
    public final void onRotationVector(RotationVectorEvent rotationVectorEvent) {
        if (!Minecraft.inGame() || activeRotator == null || rotationVectorEvent.entity != client.player)
            return;

        // Checked in inGame(), only here so ide won't complain
        assert client.player != null;

        // Sets the rotation vector, this affects Minecraft's raytrace calculation
        final float partialYaw = MovementUtils.Math.handlePartialRotation(rotationVectorEvent.tickDelta, previousRotations[0], serverRotations[0]);
        final float partialPitch = MovementUtils.Math.handlePartialRotation(rotationVectorEvent.tickDelta, previousRotations[1], serverRotations[1]);
        rotationVectorEvent.result = rotationVectorEvent.entity.getRotationVector(partialPitch, partialYaw);
    }

    @EventHandler
    public void onPreTickRaytrace(PreTickRaytraceEvent preTickRaytraceEvent) {
        if (!Minecraft.inGame())
            return;

        // Checked in inGame(), only here so ide won't complain
        assert client.player != null;

        // Server rotations are updated here, so the last server rotations are previous
        previousRotations[0] = serverRotations[0];
        previousRotations[1] = serverRotations[1];

        // Find rotators that can rotate and sort them by priority
        Optional<Rotator> rotatorOptional = rotators.stream()
                .filter(Rotator::canRotate)
                .max(Comparator.comparingInt(Rotator::getPriorityRotations));

        if(rotatorOptional.isPresent()) {
            // If a new rotator has been found, switch to it and stop back rotating
            this.activeRotator = rotatorOptional.get();
            this.corrector = activeRotator.getCorrector();
            this.backRotating = false;
        } else if (this.activeRotator != null) {
            // If a new rotator has not been found, but there is still an active one, start back rotating and stop the rotator if back rotation's been finished
            if(((activeRotator.getFlags() & RotatorFlags.DISABLE_BACK_ROTATION) != RotatorFlags.DISABLE_BACK_ROTATION) ||
                serverRotations[0] == client.player.getYaw() && serverRotations[1] == client.player.getPitch()) {
                this.activeRotator = null;
                this.backRotating = false;
            } else {
                this.backRotating = true;
            }

            if (this.activeRotator != null)
                this.corrector = activeRotator.getCorrector();
        } else {
            // If there is not an active rotator and a new one, stop back-rotations
            // This shouldn't be necessary and serves as error-correction
            this.backRotating = false;
        }

        // If there is an active rotator, then spoof the rotations
        // If not then set the server rotation the client rotations
        if (activeRotator != null) {
            spoof(true, 1.0F);
        } else {
            serverRotations[0] = client.player.getYaw();
            serverRotations[1] = client.player.getPitch();
        }

        reportedRotations[0] = serverRotations[0];
        reportedRotations[1] = serverRotations[1];
    }

    @EventHandler
    public void onStrafeInput(StrafeInputEvent strafeInputEvent) {
        if (corrector == null)
            return;

        if (activeRotator != null) {
            corrector.edit(serverRotations[0], strafeInputEvent);
            strafing = true;
        } else {
            corrector.reset();
            corrector = null;
            strafing = false;
        }
    }

    @EventHandler
    public final void onStrafe(StrafeEvent strafeEvent) {
        if (corrector == null)
            return;

        if (activeRotator != null && corrector.fixYaw()) {
            strafeEvent.yaw = serverRotations[0];
        }
    }

    @EventHandler
    public final void onJump(JumpEvent jumpEvent) {
        if (corrector == null)
            return;

        if (activeRotator != null && corrector.fixYaw()) {
            jumpEvent.yaw = serverRotations[0];
        }
    }

    /*
        The next few methods serve to apply the rotations created inhere
     */

    @EventHandler
    public void onRotationGet(RotationGetEvent rotationGetEvent) {
        if (activeRotator != null) {
            rotationGetEvent.yaw = serverRotations[0];
            rotationGetEvent.pitch = serverRotations[1];
        }
    }

    @EventHandler
    public void onRotationSet(RotationSetEvent rotationSetEvent) {
        if (rotationSetEvent.isYaw) serverRotations[0] = rotationSetEvent.yaw;
        if (rotationSetEvent.isPitch) serverRotations[1] = rotationSetEvent.pitch;
    }

    @EventHandler
    public void onSendYaw(SendYawEvent sendYawEvent) {
        if (activeRotator != null) sendYawEvent.yaw = serverRotations[0];
    }

    @EventHandler
    public void onSendPitch(SendPitchEvent sendPitchEvent) {
        if (activeRotator != null) sendPitchEvent.pitch = serverRotations[1];
    }

    public float getYaw() {
        return serverRotations[0];
    }

    public float getPitch() {
        return serverRotations[1];
    }

    public static class RotatorFlags {
        public static int NON_SILENT = 1;
        public static int DISABLE_UPDATERS = 2;
        public static int DISABLE_BACK_ROTATION = 4;
        public static int LOG = 8;
    }

}
