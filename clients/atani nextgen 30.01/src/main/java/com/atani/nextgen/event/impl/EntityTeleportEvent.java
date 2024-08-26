package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

public class EntityTeleportEvent extends Event {
    public int posX, posY, posZ;
    public float rotationYaw, rotationPitch;
    public final float fixedYaw, fixedPitch;

    public EntityTeleportEvent(EventType eventType, int posX, int posY, int posZ, float rotationYaw, float rotationPitch, float fixedYaw, float fixedPitch) {
        super(eventType);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.fixedYaw = fixedYaw;
        this.fixedPitch = fixedPitch;
    }
}
