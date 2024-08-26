package com.skidding.atlas.event.impl.input.mouse;

import com.skidding.atlas.event.Event;

public class RegisterClickEvent extends Event {

    public int button;

    public RegisterClickEvent(EventType eventType, int button) {
        super(eventType);
        this.button = button;
    }
}
