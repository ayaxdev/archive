package com.skidding.atlas.event.impl.network.packet;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class S08XRotationEvent extends Event {
    public float rotationPitch;
}
