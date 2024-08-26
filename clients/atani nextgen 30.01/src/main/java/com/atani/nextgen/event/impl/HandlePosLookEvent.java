package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;

public class HandlePosLookEvent extends Event {
    public float rotationYaw, rotationPitch;

    public HandlePosLookEvent(EventType eventType, float rotationYaw, float rotationPitch) {
        super(eventType);
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
