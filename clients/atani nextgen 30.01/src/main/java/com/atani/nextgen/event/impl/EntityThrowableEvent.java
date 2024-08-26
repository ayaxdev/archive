package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityThrowableEvent extends Event {
    public float rotationYaw, rotationPitch;
}
