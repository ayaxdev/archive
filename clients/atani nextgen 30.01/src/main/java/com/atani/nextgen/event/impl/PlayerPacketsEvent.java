package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;

public class PlayerPacketsEvent extends Event {
    public double posX, posY, posZ, motionX, motionY;
    public float rotationYaw, rotationPitch;
    public boolean onGround, sprinting;

    public PlayerPacketsEvent(EventType eventType, double posX, double posY, double posZ, double motionX, double motionY, float rotationYaw, float rotationPitch, boolean onGround, boolean sprinting) {
        super(eventType);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.motionX = motionX;
        this.motionY = motionY;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
        this.sprinting = sprinting;
    }
}
