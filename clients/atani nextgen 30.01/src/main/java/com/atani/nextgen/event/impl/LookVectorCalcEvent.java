package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LookVectorCalcEvent extends Event {
    public float rotationYaw, rotationPitch;
}
