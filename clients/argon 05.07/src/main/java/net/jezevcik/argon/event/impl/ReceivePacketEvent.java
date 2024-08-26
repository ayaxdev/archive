package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class ReceivePacketEvent extends Cancellable {

    public Packet<?> packet;
    public PacketListener packetListener;

    public ReceivePacketEvent(Packet<?> packet, PacketListener packetListener) {
        this.packet = packet;
        this.packetListener = packetListener;
    }
}