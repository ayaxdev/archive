package com.skidding.atlas.event.impl.input.mouse;

import com.skidding.atlas.event.Event;

public class RunClickEvent extends Event {
    public int leftClickCounter;

    public RunClickEvent(EventType eventType, int leftClickCounter) {
        super(eventType);
        this.leftClickCounter = leftClickCounter;
    }

}