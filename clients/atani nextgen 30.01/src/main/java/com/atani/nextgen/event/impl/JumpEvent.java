package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;

public class JumpEvent extends Event {
    public double height;
    public float rotationYaw;

    public boolean allowAmplifier = true;

    public JumpEvent(double height, float rotationYaw) {
        this.height = height;
        this.rotationYaw = rotationYaw;
    }
}
