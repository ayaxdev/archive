package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;

public class UpdateEvent extends Event {
    public float rotationYaw, rotationPitch;

    public UpdateEvent(EventType eventType, float rotationYaw, float rotationPitch) {
        super(eventType);

        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
