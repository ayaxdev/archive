package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class ReceivePacketEvent extends Event {

    public Packet<?> packet;
    public PacketListener packetListener;

    public ReceivePacketEvent(Packet<?> packet, PacketListener packetListener) {
        this.packet = packet;
        this.packetListener = packetListener;
    }
}