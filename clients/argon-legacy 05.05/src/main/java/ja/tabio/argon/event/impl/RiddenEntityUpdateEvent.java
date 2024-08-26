package ja.tabio.argon.event.impl;

import net.minecraft.entity.Entity;

public class RiddenEntityUpdateEvent {
    public final Entity entity;
    public float rotationYaw, rotationPitch;

    public RiddenEntityUpdateEvent(Entity entity, float rotationYaw, float rotationPitch) {
        this.entity = entity;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
