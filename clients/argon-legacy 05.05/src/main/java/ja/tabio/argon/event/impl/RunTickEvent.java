package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class RunTickEvent extends Event {
    public final int currentTick;

    public RunTickEvent(int currentTick) {
        this.currentTick = currentTick;
    }
}
