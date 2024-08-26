package com.atani.nextgen.processor.impl;

import com.atani.nextgen.event.Event;
import com.atani.nextgen.event.impl.*;
import com.atani.nextgen.processor.Processor;
import io.github.racoondog.norbit.EventHandler;
import lombok.Getter;

public class RotationProcessor extends Processor {

    @Getter
    private float rotationYaw, rotationPitch;
    private float prevRotationYaw, prevRotationPitch;

    public MovementFix movementFix;

    @EventHandler(priority = 9999)
    public final void onUpdate(UpdateEvent updateEvent) {
        // This changes the rotations when riding an entity
        updateEvent.rotationYaw = rotationYaw;
        updateEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onMoveFlying(MoveFlyingEvent moveFlyingEvent) {
        // This changes the yaw used to calculate strafing
        if(movementFix != MovementFix.NONE)
            moveFlyingEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onJump(JumpEvent jumpEvent) {
        // This changes the yaw used to calculate jump motion
        if(movementFix != MovementFix.NONE)
            jumpEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onPigRotation(PigRotationEvent pigRotationEvent) {
        // This fixes some weird fucking thing with pigs
        pigRotationEvent.rotationYaw = rotationYaw;
        pigRotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onDeath(DeathEvent deathEvent) {
        // This changes the yaw used to calculate motion on death
        deathEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onKnockbackModifier(KnockbackModifierEvent knockbackModifierEvent) {
        // This changes the yaw used to calculate knockback motion
        knockbackModifierEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onRenderYawHead(RenderYawHeadEvent renderYawHeadEvent) {
        // This changes the yaw being rendered
        renderYawHeadEvent.rotationYawHead = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onRenderPitchHead(RenderPitchHeadEvent renderPitchHeadEvent) {
        // This changes the pitch being rendered
        renderPitchHeadEvent.renderPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * renderPitchHeadEvent.partialTicks;
    }

    @EventHandler(priority = 9999)
    public final void onEntityUpdate(EntityUpdateEvent entityUpdateEvent) {
        // This is a fix for rendering the rotations
        entityUpdateEvent.prevRotationYawHead = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onS18(S18RotationEvent s18RotationEvent) {
        // This is a fix for if the server attempts to set rotations
        s18RotationEvent.rotationYaw = rotationYaw;
        s18RotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onKnockback(KnockbackEvent knockbackEvent) {
        // This changes the yaw used to calculate knockback motion
        knockbackEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onYawOffset(YawOffsetEvent yawOffsetEvent) {
        // This changes the yaw used to render the rotation of the player's body
        yawOffsetEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onUpdateDistance(UpdateDistanceEvent updateDistanceEvent) {
        // This is a fix for calculating the body yaw
        updateDistanceEvent.yaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onRotation(RotationEvent rotationEvent) {
        // This is the main event powering the rotation processor. You subscribe to this to change the rotations which are going to be used
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        rotationYaw = rotationEvent.rotationYaw;
        rotationPitch = rotationEvent.rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onEntityThrowable(EntityThrowableEvent entityThrowableEvent) {
        // This is a fix for throwing items
        entityThrowableEvent.rotationYaw = rotationYaw;
        entityThrowableEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onEntityTeleport(EntityTeleportEvent entityTeleportEvent) {
        // This is a fix for if the server attempts to teleport the player
        if (entityTeleportEvent.eventType == Event.EventType.POST) {
            rotationYaw = entityTeleportEvent.fixedYaw;
            rotationPitch = entityTeleportEvent.fixedPitch;
        }
    }

    @EventHandler(priority = 9999)
    public final void onS08Y(S08YRotationEvent s08YRotationEvent) {
        // This is a fix for if the server attempts to change the rotation
        s08YRotationEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = 9999)
    public final void onS08X(S08XRotationEvent s08XRotationEvent) {
        // This is a fix for if the server attempts to change the rotation
        s08XRotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onHandlePosLook(HandlePosLookEvent handlePosLookEvent) {
        // This is a fix for if the server attempts to change the rotation
        if(handlePosLookEvent.eventType == Event.EventType.MID) {
            rotationYaw = handlePosLookEvent.rotationYaw;
            rotationPitch = handlePosLookEvent.rotationPitch;
        }
    }

    @EventHandler(priority = 9999)
    public final void onItemRaytrace(ItemRaytraceEvent itemRaytraceEvent) {
        // This is a fix for throwing items
        itemRaytraceEvent.rotationYaw = rotationYaw;
        itemRaytraceEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = 9999)
    public final void onMovementInput(MovementInputEvent movementInputEvent) {
        // A silent movement fix
        if(movementFix == MovementFix.SILENT) {
            movementInputEvent.movementFix = true;
            movementInputEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = 9999)
    public final void onLookVectorCalc(LookVectorCalcEvent lookVectorCalcEvent) {
        // Changes the rotations used to calculate the look vector
        lookVectorCalcEvent.rotationYaw = rotationYaw;
        lookVectorCalcEvent.rotationPitch = rotationPitch;
    }

    public enum MovementFix {
        NONE, STRICT, SILENT;
    }

}
