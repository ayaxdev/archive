package com.skidding.atlas.event.impl.render.overlay;

import com.skidding.atlas.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
    public final ScaledResolution scaledResolution;
    public final float partialTicks;

    public Render2DEvent(EventType eventType, ScaledResolution scaledResolution, float partialTicks) {
        super(eventType);
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }
}
