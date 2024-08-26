package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
    public final ScaledResolution scaledResolution;
    public final float partialTicks;

    public Render2DEvent(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }
}
