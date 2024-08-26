package com.skidding.atlas.event.impl.network.packet;

import com.skidding.atlas.event.Event;

public class HandleS08Event extends Event {
    public float rotationYaw, rotationPitch;

    public HandleS08Event(EventType eventType, float rotationYaw, float rotationPitch) {
        super(eventType);
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
