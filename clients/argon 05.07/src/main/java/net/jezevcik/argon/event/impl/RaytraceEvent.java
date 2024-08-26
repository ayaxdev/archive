package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;

public class RaytraceEvent extends Cancellable {
    public final float tickDelta;

    public RaytraceEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
