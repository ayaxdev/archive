package com.skidding.atlas.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Event {

    public final EventType eventType;
    public boolean cancelled;

    public Event() {
        this.eventType = EventType.PRE;
    }

    public enum EventType {
        BEFORE_PRE, PRE, AFTER_PRE,
        BEFORE_MID, MID, AFTER_MID,
        BEFORE_POST, POST, AFTER_POST,
        INCOMING, OUTGOING;
    }
}
