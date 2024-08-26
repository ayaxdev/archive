package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.event.impl.player.update.UpdateEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import io.github.racoondog.norbit.EventHandler;

public class LongJumpModule extends ModuleFeature {

    public final SettingFeature<String> jumpMode = mode("Mode", "Motion", new String[]{"Motion", "AAC 3.3.12"}).build();

    public LongJumpModule() {
        super(new ModuleBuilder("LongJump", "Grants the ability to jump further distances", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public final void onMotion(WalkingPacketsEvent walkingPacketsEvent) {
        if (walkingPacketsEvent.eventType == Event.EventType.PRE) {
            switch (jumpMode.getValue()) {
                case "Motion" -> {
                    if (getPlayer().onGround && MovementUtil.INSTANCE.isMoving()) {
                        getPlayer().motionY = 0.493F;
                    } else {
                        double rotation = Math.toRadians(getPlayer().rotationYaw), x = Math.sin(rotation), z = Math.cos(rotation);

                        if (MovementUtil.INSTANCE.isMoving()) {
                            getPlayer().setPosition(getPlayer().posX - x * 0.1673, getPlayer().posY, getPlayer().posZ + z * 0.1673);
                            getPlayer().setPosition(getPlayer().posX, getPlayer().posY + 0.01557f, getPlayer().posZ);
                            getPlayer().motionY = getPlayer().motionY + 0.0155f;
                            getPlayer().speedInAir = 0.027f;
                            getPlayer().jumpMovementFactor = getPlayer().jumpMovementFactor + 0.00155f;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public final void onUpdate(UpdateEvent updateEvent) {
        if (jumpMode.getValue().equals("AAC 3.3.12")) {
            if (getPlayer().ticksExisted % 3 == 0 && !getPlayer().onGround) {
                MovementUtil.INSTANCE.teleportTo(4.5, getPlayer().posY, getPlayer().rotationYaw);
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        if (getPlayer() != null) {
            getPlayer().speedInAir = 0.02f;
            mc.timer.timerSpeed = 1f;
        }
    }
}
