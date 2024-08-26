package com.skidding.atlas.module.impl.player;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFallModule extends ModuleFeature {

    public final SettingFeature<String> noFallMode = mode("Mode", "Vanilla", new String[]{"Vanilla", "Packet", "Invalid"}).build();
    public final SettingFeature<Float> fallDistance = slider("Distance", 3, 0, 7, 0).build();


    public NoFallModule() {
        super(new ModuleBuilder("NoFall", "Minimizes or eliminates fall damage to keep you safe", ModuleCategory.PLAYER));
    }

    @EventHandler
    public final void onMotion(WalkingPacketsEvent walkingPacketsEvent) {
        if (walkingPacketsEvent.eventType == Event.EventType.PRE) {
            if (getPlayer().fallDistance > fallDistance.getValue()) {
                switch (noFallMode.getValue()) {
                    case "Vanilla" -> getPlayer().onGround = true;
                    case "Packet" ->
                            getPlayer().sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY, getPlayer().posZ, getPlayer().rotationYaw, getPlayer().rotationPitch, true));
                    case "Invalid" -> {
                        getPlayer().onGround = true;
                        getPlayer().motionY = -9999;
                    }
                }
                getPlayer().fallDistance = 0;
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
