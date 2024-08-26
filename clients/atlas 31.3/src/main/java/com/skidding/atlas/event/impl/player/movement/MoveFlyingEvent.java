package com.skidding.atlas.event.impl.player.movement;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoveFlyingEvent extends Event {
    public float strafe, forward, friction, rotationYaw;
}
