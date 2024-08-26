package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class RenderPitchHeadEvent extends Event {

    public final Entity entity;
    public float renderPitch, partialTicks;

    public RenderPitchHeadEvent(Entity entity, float renderPitch, float partialTicks) {
        this.entity = entity;
        this.renderPitch = renderPitch;
        this.partialTicks = partialTicks;
    }
}
