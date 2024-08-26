package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class EntityMovementEvent extends Event {
    public final Entity entity;
    public double motionX, motionY, motionZ;
    public boolean noClip;

    public EntityMovementEvent(Entity entity, double motionX, double motionY, double motionZ, boolean noClip) {
        this.entity = entity;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.noClip = noClip;
    }
}
