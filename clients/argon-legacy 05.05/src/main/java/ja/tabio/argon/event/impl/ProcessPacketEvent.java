package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import ja.tabio.argon.event.enums.PacketType;
import net.minecraft.network.Packet;

public class ProcessPacketEvent extends Event {
    public Packet packet;
    public final PacketType packetType;

    public ProcessPacketEvent(PacketType packetType, Packet packet) {
        this.packetType = packetType;
        this.packet = packet;
    }
}
