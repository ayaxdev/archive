package com.skidding.atlas.event.impl.player.combat;

import com.skidding.atlas.event.Event;

public class KnockbackModifierEvent extends Event {
    public boolean flag;
    public float rotationYaw;
    public boolean reduceY = false;

    public KnockbackModifierEvent(boolean flag, float rotationYaw) {
        this.flag = flag;
        this.rotationYaw = rotationYaw;
    }
}
