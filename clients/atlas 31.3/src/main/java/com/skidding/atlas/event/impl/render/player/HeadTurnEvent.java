package com.skidding.atlas.event.impl.render.player;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HeadTurnEvent extends Event {
    public float rotationYaw;
}
