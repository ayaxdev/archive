package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;

public class SendPacketEvent extends Event {

    public Packet<?> packet;
    public PacketCallbacks packetCallbacks;

    public SendPacketEvent(Packet<?> packet, PacketCallbacks packetCallbacks) {
        this.packet = packet;
        this.packetCallbacks = packetCallbacks;
    }
}