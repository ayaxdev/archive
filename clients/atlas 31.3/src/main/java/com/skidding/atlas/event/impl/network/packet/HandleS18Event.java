package com.skidding.atlas.event.impl.network.packet;

import com.skidding.atlas.event.Event;

public class HandleS18Event extends Event {
    public int posX, posY, posZ;
    public float rotationYaw, rotationPitch;
    public final float fixedYaw, fixedPitch;

    public HandleS18Event(EventType eventType, int posX, int posY, int posZ, float rotationYaw, float rotationPitch, float fixedYaw, float fixedPitch) {
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
