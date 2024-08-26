package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class MoveCameraEvent extends Event {
    public final float tickDelta;

    public MoveCameraEvent(float tickDelta) {
        this.tickDelta = tickDelta;
    }
}
