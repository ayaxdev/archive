package ja.tabio.argon.event.impl;

import net.minecraft.entity.Entity;

public class RenderYawHeadEvent {
    public final Entity entity;
    public float rotationYawHead;

    public RenderYawHeadEvent(Entity entity, float rotationYawHead) {
        this.entity = entity;
        this.rotationYawHead = rotationYawHead;
    }
}
