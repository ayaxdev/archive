package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class MoveFlyingEvent extends Event {
    public final Entity entity;
    public float strafe, forward, friction, rotationYaw;

    public MoveFlyingEvent(Entity entity, float strafe, float forward, float friction, float rotationYaw) {
        this.entity = entity;
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
        this.rotationYaw = rotationYaw;
    }
}
