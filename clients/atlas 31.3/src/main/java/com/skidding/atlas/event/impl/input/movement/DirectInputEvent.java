package com.skidding.atlas.event.impl.input.movement;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DirectInputEvent extends Event {
    public boolean left;
    public boolean right;
    public boolean backward;
    public boolean forward;
    public boolean sneak;
    public boolean jump;

}
