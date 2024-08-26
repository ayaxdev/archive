package ja.tabio.argon.processor.impl;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.*;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.processor.annotation.ProcessorData;

import java.util.List;

@ProcessorData(name = "Rotations")
public class RotationProcessor extends Processor {

    public static boolean correctMovementAngle,
            correctInput;

    private static float rotationYaw, rotationPitch,
            prevRotationYaw, prevRotationPitch;

    private List<PlayerRotationEvent.RotationModifier> lastRotationModifiers;
    private boolean rotating;

    @EventHandler(priority = -9999)
    public final void onUpdate(PlayerUpdateEvent playerUpdateEvent) {
        if (playerUpdateEvent.type == PlayerType.SERVER) {
            playerUpdateEvent.rotationYaw = rotationYaw;
            playerUpdateEvent.rotationPitch = rotationPitch;
        } else if (playerUpdateEvent.type == PlayerType.LOCAL && playerUpdateEvent.stage == Stage.POST) {
            mc.thePlayer.prevRotationYawHead = rotationYaw;
            prevRotationPitch = rotationPitch;
        }
    }

    @EventHandler(priority = -9999)
    public final void onMoveFlying(MoveFlyingEvent moveFlyingEvent) {
        if (moveFlyingEvent.entity == mc.thePlayer && correctMovementAngle)
            moveFlyingEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onJump(final JumpEvent jumpEvent) {
        if (jumpEvent.entity == mc.thePlayer && correctMovementAngle) {
            jumpEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onRiddenEntityUpdate(final RiddenEntityUpdateEvent riddenEntityUpdateEvent) {
        if (riddenEntityUpdateEvent.entity == mc.thePlayer && correctMovementAngle) {
            riddenEntityUpdateEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onDeath(final DeathEvent deathEvent) {
        if (deathEvent.entity == mc.thePlayer && correctMovementAngle) {
            deathEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onKnockback(final KnockbackModifierEvent knockbackModifierEvent) {
        if (knockbackModifierEvent.entity == mc.thePlayer && correctMovementAngle) {
            knockbackModifierEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onRenderYawHead(final RenderYawHeadEvent renderYawHeadEvent) {
        if (renderYawHeadEvent.entity == mc.getRenderManager().livingPlayer) {
            renderYawHeadEvent.rotationYawHead = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onRenderPitchHead(final RenderPitchHeadEvent renderPitchHeadEvent) {
        if (renderPitchHeadEvent.entity == mc.getRenderManager().livingPlayer) {
            renderPitchHeadEvent.renderPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * renderPitchHeadEvent.partialTicks;
        }
    }

    @EventHandler(priority = -9999)
    public final void onS18(S18RotationEvent s18RotationEvent) {
        s18RotationEvent.rotationYaw = rotationYaw;
        s18RotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onKnockback(KnockbackEvent knockbackEvent) {
        if (knockbackEvent.entity == mc.thePlayer && correctMovementAngle)
            knockbackEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onHeadTurn(EntityHeadTurnEvent entityHeadTurnEvent) {
        if (entityHeadTurnEvent.entity == mc.thePlayer) {
            entityHeadTurnEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onHeadTurn(EntityHeadTurnDistanceEvent entityHeadTurnDistanceEvent) {
        if (entityHeadTurnDistanceEvent.entity == mc.thePlayer) {
            entityHeadTurnDistanceEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onRotation(PlayerRotationEvent playerRotationEvent) {
        float yaw = playerRotationEvent.getRotationYaw(),
                pitch = playerRotationEvent.getRotationPitch();

        if (playerRotationEvent.legit && rotating) {
            for (PlayerRotationEvent.RotationModifier rotationModifier : this.lastRotationModifiers) {
                final float[] next = rotationModifier.run(new float[] {
                        RotationProcessor.rotationYaw, RotationProcessor.rotationPitch
                }, new float[] {
                        yaw, pitch
                });

                yaw = next[0];
                pitch = next[1];
            }
        } else if (!playerRotationEvent.legit) {
            this.lastRotationModifiers = playerRotationEvent.modifiers;

            for (PlayerRotationEvent.RotationModifier rotationModifier : playerRotationEvent.modifiers) {
                final float[] next = rotationModifier.run(new float[] {
                        RotationProcessor.rotationYaw, RotationProcessor.rotationPitch
                }, new float[] {
                        yaw, pitch
                });

                yaw = next[0];
                pitch = next[1];
            }
        }

        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        rotationYaw = playerRotationEvent.getRotationYaw();
        rotationPitch = playerRotationEvent.getRotationPitch();

        if (playerRotationEvent.legit) {
            if (yaw == playerRotationEvent.getRotationYaw() && pitch == playerRotationEvent.getRotationPitch()) {
                rotating = false;
                lastRotationModifiers = null;
            }
        } else {
            rotating = true;
        }
    }

    @EventHandler(priority = -9999)
    public final void onThrowableInstance(CreateThrowableEntityEvent createThrowableEntityEvent) {
        if (createThrowableEntityEvent.entity == mc.thePlayer) {
            createThrowableEntityEvent.rotationYaw = rotationYaw;
            createThrowableEntityEvent.rotationPitch = rotationPitch;
        }
    }

    @EventHandler(priority = -9999)
    public final void onHandleS18(HandleS18Event handleS18Event) {
        if (handleS18Event.entity == mc.thePlayer && handleS18Event.stage == Stage.POST) {
            rotationYaw = handleS18Event.fixedYaw;
            rotationPitch = handleS18Event.fixedPitch;
        }
    }

    @EventHandler(priority = -9999)
    public final void onS08Y(S08YRotationEvent s08YRotationEvent) {
        s08YRotationEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onS08X(S08XRotationEvent s08XRotationEvent) {
        s08XRotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onHandlePosLook(HandleS08Event handleS08Event) {
        if (handleS08Event.stage == Stage.MID) {
            rotationYaw = handleS08Event.rotationYaw;
            rotationPitch = handleS08Event.rotationPitch;
        }
    }


    @EventHandler(priority = -9999)
    public final void onItemRaytrace(ItemRaytraceEvent itemRaytraceEvent) {
        itemRaytraceEvent.rotationYaw = rotationYaw;
        itemRaytraceEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onMovementInput(StrafeInputEvent strafeInputEvent) {
        if (correctMovementAngle && correctInput) {
            strafeInputEvent.movementFix = true;
            strafeInputEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onLookVectorCalc(LookCalculationEvent lookCalculationEvent) {
        lookCalculationEvent.rotationYaw = rotationYaw;
        lookCalculationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onDropItem(DropItemEvent dropItemEvent) {
        dropItemEvent.rotationYaw = rotationYaw;
        dropItemEvent.rotationPitch = rotationPitch;
    }

    public static float getRotationPitch() {
        return rotationPitch;
    }

    public static float getPrevRotationYaw() {
        return prevRotationYaw;
    }

    public static float getPrevRotationPitch() {
        return prevRotationPitch;
    }

    public static float getRotationYaw() {
        return rotationYaw;
    }

    public static class SensitivityPatch extends PlayerRotationEvent.RotationModifier {

        public boolean a3;

        public SensitivityPatch(boolean a3) {
            this.a3 = a3;
        }

        @Override
        public float[] run(float[] currentAngle, float[] nextAngle) {
            return applyMouseSensitivity(nextAngle[0], nextAngle[1], currentAngle[0], currentAngle[1], a3);
        }

        public static float[] applyMouseSensitivity(float yaw, float pitch, float lastYaw, float lastPitch, boolean a3) {
            final float sensitivity = Math.max(0.0070422534F, mc.gameSettings.mouseSensitivity);

            float deltaYaw = (yaw - lastYaw) / (sensitivity / 2);
            float deltaPitch = ((pitch - lastPitch) / (sensitivity / 2)) * -1;

            if (a3) {
                deltaYaw -= (float) (deltaYaw % 0.5 + 0.25);
                deltaPitch -= (float) (deltaPitch % 0.5 + 0.25);
            }

            final float f = sensitivity * 0.6F + 0.2F;
            final float f1 = f * f * f * 8F;
            final float f2 = deltaYaw * f1;
            final float f3 = deltaPitch * f1;

            final float endYaw = (float) ((double)lastYaw + (double)f2 * 0.15);
            final float endPitch = (float) ((double)lastPitch - (double)f3 * 0.15);

            return new float[] {endYaw, endPitch};
        }
    }
}
