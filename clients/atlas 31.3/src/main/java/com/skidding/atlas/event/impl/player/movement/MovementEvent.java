package com.skidding.atlas.event.impl.player.movement;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MovementEvent extends Event {
    public double motionX, motionY, motionZ;
}
