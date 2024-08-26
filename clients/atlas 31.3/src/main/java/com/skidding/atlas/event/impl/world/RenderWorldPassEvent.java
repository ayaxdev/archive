package com.skidding.atlas.event.impl.world;

import com.skidding.atlas.event.Event;

public class RenderWorldPassEvent extends Event {
    public final float partialTicks;

    public RenderWorldPassEvent(EventType eventType, float partialTicks) {
        super(eventType);
        this.partialTicks = partialTicks;
    }
}
