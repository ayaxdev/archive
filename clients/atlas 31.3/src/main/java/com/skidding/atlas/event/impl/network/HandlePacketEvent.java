package com.skidding.atlas.event.impl.network;

import com.skidding.atlas.event.Event;
import net.minecraft.network.Packet;

public class HandlePacketEvent extends Event {
    public Packet<?> packet;

    public HandlePacketEvent(EventType eventType, Packet<?> packet) {
        super(eventType);
        this.packet = packet;
    }
}
