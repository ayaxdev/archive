package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class RaytraceEvent extends Event {
    public final float tickDelta;

    public RaytraceEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
