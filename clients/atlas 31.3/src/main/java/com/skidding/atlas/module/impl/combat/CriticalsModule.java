package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.impl.player.action.AttackEntityEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.client.C03PacketPlayer;

public class CriticalsModule extends ModuleFeature {

    public final SettingFeature<String> criticalsMode = mode("Mode", "Packet", new String[]{"Packet", "AAC 5.0.4", "AAC 5.0.0", "NCP Latest", "Vulcan"}).build();

    public CriticalsModule() {
        super(new ModuleBuilder("Criticals", "Ensures all your attacks result in critical hits", ModuleCategory.COMBAT));
    }

    private int attacked = 0, ticks = 0;

    @EventHandler
    public void onAttack(AttackEntityEvent event) {
        attacked++;
        switch (criticalsMode.getValue()) {
            case "Vulcan" -> {
                if (attacked > 7) {
                    sendPositionPacket(0.16477328182606651, false);
                    sendPositionPacket(0.08307781780646721, false);
                    sendPositionPacket(0.0030162615090425808, false);
                    attacked = 0;
                }
            }
            case "NCP Latest" -> {
                if (attacked >= 5) {
                    sendPositionPacket(0.00001058293536, false);
                    sendPositionPacket(0.00000916580235, false);
                    sendPositionPacket(0.00000010371854, false);
                    attacked = 0;
                }
            }
            case "AAC 5.0.0" -> {
                sendPositionPacket(0.0625, false);
                sendPositionPacket(0.0433, false);
                sendPositionPacket(0.2088, false);
                sendPositionPacket(0.9963, false);
            }
            case "AAC 5.0.4" -> {
                sendPositionPacket(0.00133545, false);
                sendPositionPacket(-0.000000433, false);
            }
            case "Packet" -> {
                sendPositionPacket(0.0625, true);
                sendPositionPacket(false);
                sendPositionPacket(1.1E-5, false);
                sendPositionPacket(false);
                sendPositionPacket(false);
                sendPositionPacket(false);
                sendPositionPacket(false);
            }
        }
    }

    public void sendPositionPacket(boolean ground) {
        this.sendPositionPacket(0, 0, 0, ground);
    }

    public void sendPositionPacket(double yOffset, boolean ground) {
        this.sendPositionPacket(0, yOffset, 0, ground);
    }

    public void sendPositionPacket(double xOffset, double yOffset, double zOffset, boolean ground) {
        double x = getPlayer().posX + xOffset;
        double y = getPlayer().posY + yOffset;
        double z = getPlayer().posZ + zOffset;
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        attacked = 0;
    }
}
