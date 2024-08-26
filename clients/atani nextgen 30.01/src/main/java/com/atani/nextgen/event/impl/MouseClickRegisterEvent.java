package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;

public class MouseClickRegisterEvent extends Event {

    public int button;

    public MouseClickRegisterEvent(EventType eventType, int button) {
        super(eventType);
        this.button = button;
    }
}
