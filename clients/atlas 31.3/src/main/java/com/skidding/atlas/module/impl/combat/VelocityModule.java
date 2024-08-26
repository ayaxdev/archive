package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.input.movement.DirectInputEvent;
import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.event.impl.player.action.AttackEntityEvent;
import com.skidding.atlas.event.impl.player.combat.KnockbackModifierEvent;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.RotationProcessor;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public final class VelocityModule extends ModuleFeature {

    public final SettingFeature<String> mode = mode("Mode", "Simple", new String[]{"Simple", "Legit-Reduce", "AAC 4.x.x", "AAC 3.3.12", "AAC 3.3.14", "Vulcan", "Intave", "Intave-Reverse"}).build();

    // Simple
    public final SettingFeature<Boolean> cancelKnockBack = check("Cancel Knock-back", true)
            .addDependency(mode, "Simple").build();
    public final SettingFeature<Float> vertical = slider("Vertical", 0, 0, 100, 1)
            .addDependency(cancelKnockBack, false).addDependency(mode, "Simple").build();
    public final SettingFeature<Float> horizontal = slider("Horizontal", 0, 0, 100, 1)
            .addDependency(cancelKnockBack, false).addDependency(mode, "Simple").build();

    // Legit-Reduce
    public final SettingFeature<Float> multiplier = slider("Multiplier", 0.6f, 0, 1, 1)
            .addDependency(mode, "Legit-Reduce").build();

    private final RotationProcessor rotationProcessor = ProcessorManager.getSingleton().getByClass(RotationProcessor.class);
    private boolean intaveActive = false;
    private int intaveJumps = 0;

    public VelocityModule() {
        super(new ModuleBuilder("Velocity", "Alters the amount of knockback received from attacks", ModuleCategory.COMBAT));
    }

    @EventHandler
    public void onPacket(HandlePacketEvent handlePacketEvent) {
        if (handlePacketEvent.eventType == Event.EventType.INCOMING) {
            if (handlePacketEvent.packet instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == getPlayer().getEntityId()) {
                switch (mode.getValue()) {
                    case "Simple" -> {
                        if (cancelKnockBack.getValue()) {
                            handlePacketEvent.cancelled = true;
                            return;
                        }
                        s12.motionX = (int) (s12.motionX * (horizontal.getValue() / 100));
                        s12.motionY = (int) (s12.motionY * (vertical.getValue() / 100));
                        s12.motionZ = (int) (s12.motionZ * (horizontal.getValue() / 100));
                    }

                    case "Vulcan" -> handlePacketEvent.cancelled = true;
                    case "Intave", "Intave-Reverse" -> intaveActive = true;
                }
            }
        } else if (handlePacketEvent.eventType == Event.EventType.OUTGOING) {
            if (handlePacketEvent.packet instanceof C0FPacketConfirmTransaction) {
                if (mode.getValue().equalsIgnoreCase("Vulcan")) {
                    if (getPlayer() != null && getWorld() != null && getPlayer().hurtTime > 0) {
                        handlePacketEvent.cancelled = true;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKnockback(KnockbackModifierEvent knockbackModifierEvent) {
        switch (mode.getValue()) {
            case "Legit-Reduce" -> {
                if (!knockbackModifierEvent.flag) {
                    getPlayer().motionX *= multiplier.getValue();
                    getPlayer().motionZ *= multiplier.getValue();
                    getPlayer().setSprinting(false);
                }
            }
            case "Intave", "Intave-Reverse" -> {
                knockbackModifierEvent.reduceY = true;

                if (!mode.getValue().equalsIgnoreCase("Intave-Reverse") || intaveActive) {
                    getPlayer().motionX *= 0.6f;
                    getPlayer().motionZ *= 0.6f;
                }
            }
        }
    }

    @EventHandler
    public void onSendMovePackets(WalkingPacketsEvent walkingPacketsEvent) {
        switch (mode.getValue()) {
            case "AAC 4.x.x" -> {
                if (walkingPacketsEvent.eventType == Event.EventType.MID) {
                    if (getPlayer().hurtTime > 5) {
                        getPlayer().onGround = true;
                        MovementUtil.INSTANCE.stopWalk();
                    } else if (getPlayer().hurtTime != 0) {
                        MovementUtil.INSTANCE.resumeWalk();
                    }
                }
            }
            case "AAC 3.3.12" -> {
                if (getPlayer().hurtTime > 0 && walkingPacketsEvent.eventType == Event.EventType.MID) {
                    getPlayer().motionX *= 0.8;
                    getPlayer().motionZ *= 0.8;
                    getPlayer().motionY *= 1;
                }
            }
            case "AAC 3.3.14" -> {
                if (walkingPacketsEvent.eventType == Event.EventType.MID && getPlayer().hurtTime > 0) {
                    getPlayer().setVelocity(0, 0, 0);
                }
            }
            case "Intave", "Intave-Reverse" -> {
                if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && getPlayer().hurtTime == 9 && !getPlayer().isBurning() && intaveJumps % 2 == 0) {
                    getPlayer().movementInput.jump = true;
                    intaveJumps++;
                }
            }
        }
    }

    @EventHandler
    public void onMoveButton(DirectInputEvent directInputEvent) {
        if (mode.getValue().equalsIgnoreCase("Intave")) {
            if (getPlayer().hurtTime > 0 && mc.objectMouseOver.entityHit != null) {
                directInputEvent.forward = true;
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        if (getPlayer().hurtTime > 0 && mode.getValue().equalsIgnoreCase("Intave-Reverse") || mode.getValue().equalsIgnoreCase("Intave")) {
            getPlayer().setSprinting(false);

            if (getPlayer().hurtTime <= 6 && mode.getValue().equalsIgnoreCase("Intave-Reverse")) {
                if (intaveActive) {
                    getPlayer().motionX = -Math.sin(Math.toRadians(rotationProcessor.getRotationYaw())) * 0.02f;
                    getPlayer().motionZ = Math.cos(Math.toRadians(rotationProcessor.getRotationYaw())) * 0.02f;

                    intaveActive = false;
                }
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
