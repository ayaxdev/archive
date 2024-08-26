package com.skidding.atlas.event.impl.player.update;

import com.skidding.atlas.event.Event;

public class UpdateEvent extends Event {
    public float rotationYaw, rotationPitch;

    public UpdateEvent(EventType eventType, float rotationYaw, float rotationPitch) {
        super(eventType);

        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
