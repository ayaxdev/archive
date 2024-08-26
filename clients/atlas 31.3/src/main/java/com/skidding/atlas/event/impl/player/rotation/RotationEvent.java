package com.skidding.atlas.event.impl.player.rotation;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RotationEvent extends Event {
    public float rotationYaw, rotationPitch;
}
