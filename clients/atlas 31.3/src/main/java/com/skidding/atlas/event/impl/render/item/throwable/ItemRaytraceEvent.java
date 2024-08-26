package com.skidding.atlas.event.impl.render.item.throwable;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ItemRaytraceEvent extends Event {
    public float rotationYaw, rotationPitch;
}
