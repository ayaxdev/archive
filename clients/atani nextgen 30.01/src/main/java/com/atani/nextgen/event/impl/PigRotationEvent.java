package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

// This event solves the extremely common problem of when you're riding a pig while using a KillAura
@AllArgsConstructor
public class PigRotationEvent extends Event {
    public float rotationYaw, rotationPitch;
}
