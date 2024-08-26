package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MovementInputEvent extends Event {
    public float moveForward, moveStrafe, rotationYaw;
    public final float changeYaw;
    public boolean movementFix, fixYaw, sneak;
}
