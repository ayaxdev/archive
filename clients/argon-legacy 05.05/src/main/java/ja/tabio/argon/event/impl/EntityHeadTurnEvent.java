package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class EntityHeadTurnEvent extends Event {
    public final Entity entity;
    public float rotationYaw;

    public EntityHeadTurnEvent(Entity entity, float rotationYaw) {
        this.entity = entity;
        this.rotationYaw = rotationYaw;
    }
}
