package com.skidding.atlas.event.impl.player.misc;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ThrowableInstanceEvent extends Event {
    public float rotationYaw, rotationPitch;
}
