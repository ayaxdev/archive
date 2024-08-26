package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;

public class SendPacketEvent extends Cancellable {

    public Packet<?> packet;
    public PacketCallbacks packetCallbacks;

    public SendPacketEvent(Packet<?> packet, PacketCallbacks packetCallbacks) {
        this.packet = packet;
        this.packetCallbacks = packetCallbacks;
    }
}