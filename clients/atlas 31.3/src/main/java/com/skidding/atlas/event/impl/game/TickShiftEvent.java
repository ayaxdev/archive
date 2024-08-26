package com.skidding.atlas.event.impl.game;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TickShiftEvent extends Event {
    public int ticks;
}
