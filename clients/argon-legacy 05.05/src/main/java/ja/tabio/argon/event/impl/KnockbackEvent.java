package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class KnockbackEvent extends Event {
    public final Entity entity;
    public float rotationYaw;

    public KnockbackEvent(Entity entity, float rotationYaw) {
        this.entity = entity;
        this.rotationYaw = rotationYaw;
    }
}
