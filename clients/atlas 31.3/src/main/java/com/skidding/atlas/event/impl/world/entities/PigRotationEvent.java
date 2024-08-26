package com.skidding.atlas.event.impl.world.entities;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

// This event solves the extremely common problem of when you're riding a pig while using KillAura
@AllArgsConstructor
public class PigRotationEvent extends Event {
    public float rotationYaw, rotationPitch;
}
