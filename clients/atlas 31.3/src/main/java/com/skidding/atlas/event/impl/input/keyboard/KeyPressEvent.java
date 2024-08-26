package com.skidding.atlas.event.impl.input.keyboard;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeyPressEvent extends Event {
    public int key;
}
