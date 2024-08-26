package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;

public class ClickCallEvent extends Event {
    public int leftClickCounter;

    public ClickCallEvent(EventType eventType, int leftClickCounter) {
        super(eventType);
        this.leftClickCounter = leftClickCounter;
    }

}