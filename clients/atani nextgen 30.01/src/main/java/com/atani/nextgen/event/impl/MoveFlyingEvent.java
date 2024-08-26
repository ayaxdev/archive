package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoveFlyingEvent extends Event {
    public float strafe, forward, friction, rotationYaw;
}
