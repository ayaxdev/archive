package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MovementEvent extends Event {
    public double motionX, motionY, motionZ;
}
