package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.impl.player.action.AttackEntityEvent;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.PlayerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;

public final class MoreKnockbackModule extends ModuleFeature {

    public SettingFeature<String> mode = mode("Mode", "Simple", new String[]{"Simple", "W-Tap", "Silent"}).build();
    public SettingFeature<String> silentMode = mode("Silent mode", "Packet", new String[]{"Packet", "Double-packet", "Semi-silent"})
            .addDependency(mode, "Silent").build();

    private int ticks = 0;
    private EntityLivingBase entity = null;

    public MoreKnockbackModule() {
        super(new ModuleBuilder("MoreKnockback", "Makes you deal more knockback", ModuleCategory.COMBAT));
    }

    @EventHandler
    public void onPlayerPackets(WalkingPacketsEvent walkingPacketsEvent) {
        if (mode.getValue().equalsIgnoreCase("W-Tap")) {
            if (entity != null) {
                if (entity.hurtTime >= 0 && getPlayer().hurtTime == 0) {
                    if (getPlayer().getDistanceToEntity(entity) > 1.5 && getPlayer().getDistanceToEntity(entity) < 2) {
                        if (ticks < 2) {
                            unsprint();
                        } else {
                            sprint();
                        }
                    }
                    if (getPlayer().getDistanceToEntity(entity) > 2 && getPlayer().getDistanceToEntity(entity) < 3) {
                        if (ticks < 3) {
                            unsprint();
                        } else {
                            sprint();
                        }
                    }
                    if (getPlayer().getDistanceToEntity(entity) > 3) {
                        if (ticks < 4) {
                            unsprint();
                        } else {
                            sprint();
                        }
                    }
                } else {
                    sprint();
                }
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        switch (mode.getValue()) {
            case "Simple" -> PlayerUtil.shouldSprintReset = true;
            case "W-Tap" -> {
                PlayerUtil.shouldSprintReset = true;
                if (attackEntityEvent.target instanceof EntityLivingBase entityLivingBase) {
                    entity = entityLivingBase;
                    ticks = 0;
                }
            }
            case "Silent" -> {
                switch (silentMode.getValue()) {
                    case "Packet":
                        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SPRINTING));
                        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SPRINTING));
                        getPlayer().serverSprintState = true;
                        break;
                    case "Double-packet":
                        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SPRINTING));
                        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SPRINTING));
                        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.STOP_SPRINTING));
                        getPlayer().sendQueue.addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SPRINTING));
                        getPlayer().serverSprintState = true;
                        break;
                    case "Semi-silent":
                    case "SinglePacket": {
                        if (getPlayer().isSprinting()) {
                            getPlayer().setSprinting(false);
                        }
                        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(getPlayer(), C0BPacketEntityAction.Action.START_SPRINTING));
                        getPlayer().serverSprintState = true;
                        break;
                    }
                }
            }
        }
    }

    private void sprint() {
        if (mc.currentScreen == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
        }
    }

    private void unsprint() {
        ticks++;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
