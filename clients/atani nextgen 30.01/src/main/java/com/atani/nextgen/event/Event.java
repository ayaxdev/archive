package com.atani.nextgen.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Event {

    public final EventType eventType;
    public boolean cancelled;

    public Event() {
        this.eventType = EventType.PRE;
    }

    public enum EventType {
        PRE, MID, POST, INCOMING, OUTGOING;
    }
}
