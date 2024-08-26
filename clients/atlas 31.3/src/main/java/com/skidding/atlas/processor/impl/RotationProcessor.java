package com.skidding.atlas.processor.impl;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.network.packet.*;
import com.skidding.atlas.event.impl.player.action.DropItemEvent;
import com.skidding.atlas.event.impl.player.combat.KnockbackEvent;
import com.skidding.atlas.event.impl.player.combat.KnockbackModifierEvent;
import com.skidding.atlas.event.impl.player.misc.ThrowableInstanceEvent;
import com.skidding.atlas.event.impl.player.movement.JumpEvent;
import com.skidding.atlas.event.impl.player.movement.MoveFlyingEvent;
import com.skidding.atlas.event.impl.input.movement.StrafeInputEvent;
import com.skidding.atlas.event.impl.player.rotation.LookCalculationEvent;
import com.skidding.atlas.event.impl.player.rotation.RotationEvent;
import com.skidding.atlas.event.impl.player.state.DeathEvent;
import com.skidding.atlas.event.impl.player.update.EntityTickEvent;
import com.skidding.atlas.event.impl.player.update.UpdateEvent;
import com.skidding.atlas.event.impl.render.item.throwable.ItemRaytraceEvent;
import com.skidding.atlas.event.impl.render.player.HeadTurnEvent;
import com.skidding.atlas.event.impl.render.player.RenderPitchHeadEvent;
import com.skidding.atlas.event.impl.render.player.RenderYawHeadEvent;
import com.skidding.atlas.event.impl.world.entities.PigRotationEvent;
import com.skidding.atlas.processor.Processor;
import io.github.racoondog.norbit.EventHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RotationProcessor extends Processor {

    @Getter
    private float rotationYaw, rotationPitch;
    private float prevRotationYaw, prevRotationPitch;

    public MovementFix movementFix = MovementFix.NONE;

    @EventHandler(priority = -9999)
    public final void onUpdate(UpdateEvent updateEvent) {
        // This changes the rotations when riding an entity
        updateEvent.rotationYaw = rotationYaw;
        updateEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onMoveFlying(MoveFlyingEvent moveFlyingEvent) {
        // This changes the yaw used to calculate strafing
        if(movementFix != MovementFix.NONE)
            moveFlyingEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onJump(JumpEvent jumpEvent) {
        // This changes the yaw used to calculate jump motion
        if(movementFix != MovementFix.NONE)
            jumpEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onPigRotation(PigRotationEvent pigRotationEvent) {
        // This fixes some weird fucking thing with pigs
        pigRotationEvent.rotationYaw = rotationYaw;
        pigRotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onDeath(DeathEvent deathEvent) {
        // This changes the yaw used to calculate motion on death
        deathEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onKnockbackModifier(KnockbackModifierEvent knockbackModifierEvent) {
        // This changes the yaw used to calculate knockback motion
        knockbackModifierEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onRenderYawHead(RenderYawHeadEvent renderYawHeadEvent) {
        // This changes the yaw being rendered
        renderYawHeadEvent.rotationYawHead = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onRenderPitchHead(RenderPitchHeadEvent renderPitchHeadEvent) {
        // This changes the pitch being rendered
        renderPitchHeadEvent.renderPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * renderPitchHeadEvent.partialTicks;
    }

    @EventHandler(priority = -9999)
    public final void onEntityUpdate(EntityTickEvent entityTickEvent) {
        // This is a fix for rendering the rotations
        entityTickEvent.prevRotationYawHead = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onS18(S18RotationEvent s18RotationEvent) {
        // This is a fix for if the server attempts to set rotations
        s18RotationEvent.rotationYaw = rotationYaw;
        s18RotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onKnockback(KnockbackEvent knockbackEvent) {
        // This changes the yaw used to calculate knockback motion
        knockbackEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onYawOffset(HeadTurnEvent headTurnEvent) {
        // This changes the yaw used to render the rotation of the player's body
        headTurnEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onUpdateDistance(RenderPitchHeadEvent.HeadTurnDistanceEvent headTurnDistanceEvent) {
        // This is a fix for calculating the body yaw
        headTurnDistanceEvent.yaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onRotation(RotationEvent rotationEvent) {
        // This is the main event powering the rotation processor. You subscribe to this to change the rotations which are going to be used
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        rotationYaw = rotationEvent.rotationYaw;
        rotationPitch = rotationEvent.rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onEntityThrowable(ThrowableInstanceEvent throwableInstanceEvent) {
        // This is a fix for throwing items
        throwableInstanceEvent.rotationYaw = rotationYaw;
        throwableInstanceEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onEntityTeleport(HandleS18Event handleS18Event) {
        // This is a fix for if the server attempts to teleport the player
        if (handleS18Event.eventType == Event.EventType.POST) {
            rotationYaw = handleS18Event.fixedYaw;
            rotationPitch = handleS18Event.fixedPitch;
        }
    }

    @EventHandler(priority = -9999)
    public final void onS08Y(S08YRotationEvent s08YRotationEvent) {
        // This is a fix for if the server attempts to change the rotation
        s08YRotationEvent.rotationYaw = rotationYaw;
    }

    @EventHandler(priority = -9999)
    public final void onS08X(S08XRotationEvent s08XRotationEvent) {
        // This is a fix for if the server attempts to change the rotation
        s08XRotationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onHandlePosLook(HandleS08Event handleS08Event) {
        // This is a fix for if the server attempts to change the rotation
        if(handleS08Event.eventType == Event.EventType.MID) {
            rotationYaw = handleS08Event.rotationYaw;
            rotationPitch = handleS08Event.rotationPitch;
        }
    }

    @EventHandler(priority = -9999)
    public final void onItemRaytrace(ItemRaytraceEvent itemRaytraceEvent) {
        // This is a fix for throwing items
        itemRaytraceEvent.rotationYaw = rotationYaw;
        itemRaytraceEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onMovementInput(StrafeInputEvent strafeInputEvent) {
        // A silent movement fix
        if(movementFix == MovementFix.SILENT) {
            strafeInputEvent.movementFix = true;
            strafeInputEvent.rotationYaw = rotationYaw;
        }
    }

    @EventHandler(priority = -9999)
    public final void onLookVectorCalc(LookCalculationEvent lookCalculationEvent) {
        // Changes the rotations used to calculate the look vector
        lookCalculationEvent.rotationYaw = rotationYaw;
        lookCalculationEvent.rotationPitch = rotationPitch;
    }

    @EventHandler(priority = -9999)
    public final void onDropItem(DropItemEvent dropItemEvent) {
        // Changes the rotation in which items are dropped
        dropItemEvent.rotationYaw = rotationYaw;
        dropItemEvent.rotationPitch = rotationPitch;
    }

    @RequiredArgsConstructor
    public enum MovementFix {
        NONE("None"),
        STRICT("Strict"),
        SILENT("Silent");

        public final String name;

        @Override
        public String toString() {
            return name;
        }
    }

}
