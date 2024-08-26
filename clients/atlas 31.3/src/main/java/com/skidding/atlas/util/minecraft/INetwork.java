package com.skidding.atlas.util.minecraft;

import net.minecraft.network.Packet;

public interface INetwork extends IMinecraft {

    default void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    default void sendPacketUnlogged(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

}