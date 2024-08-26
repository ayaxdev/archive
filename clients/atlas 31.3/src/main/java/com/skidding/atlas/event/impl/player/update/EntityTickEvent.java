package com.skidding.atlas.event.impl.player.update;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityTickEvent extends Event {
    public float prevRotationYawHead;
}
