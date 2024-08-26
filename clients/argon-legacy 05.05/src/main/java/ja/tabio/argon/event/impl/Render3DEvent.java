package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class Render3DEvent extends Event {
    public final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
