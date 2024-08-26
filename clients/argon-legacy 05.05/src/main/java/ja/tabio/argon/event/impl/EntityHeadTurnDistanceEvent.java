package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class EntityHeadTurnDistanceEvent extends Event {
    public final Entity entity;
    public float rotationYaw;

    public EntityHeadTurnDistanceEvent(Entity entity, float rotationYaw) {
        this.entity = entity;
        this.rotationYaw = rotationYaw;
    }
}
