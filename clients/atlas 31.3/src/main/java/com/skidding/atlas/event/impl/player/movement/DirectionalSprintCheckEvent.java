package com.skidding.atlas.event.impl.player.movement;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DirectionalSprintCheckEvent extends Event {
    public boolean directionCheck;
}
