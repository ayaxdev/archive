package com.skidding.atlas.event.impl.input.movement;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StrafeInputEvent extends Event {
    public float moveForward, moveStrafe, rotationYaw;
    public final float changeYaw;
    public boolean movementFix, fixYaw, sneak;
}
