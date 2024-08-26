package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class S18RotationEvent extends Event {
    public float rotationYaw, rotationPitch;
}
