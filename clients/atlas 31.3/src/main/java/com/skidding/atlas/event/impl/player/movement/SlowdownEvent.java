package com.skidding.atlas.event.impl.player.movement;

import com.skidding.atlas.event.Event;

public class SlowdownEvent extends Event {
    public float strafe, forward;
    public boolean sprint = false;

    public SlowdownEvent(float strafe, float forward) {
        this.strafe = strafe;
        this.forward = forward;
    }
}
