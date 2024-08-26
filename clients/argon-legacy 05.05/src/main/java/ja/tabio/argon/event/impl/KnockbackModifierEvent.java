package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class KnockbackModifierEvent extends Event {
    public final Entity entity;
    public boolean knockback;
    public float rotationYaw;
    public boolean reduceY = false;

    public KnockbackModifierEvent(Entity entity, boolean knockback, float rotationYaw) {
        this.entity = entity;
        this.knockback = knockback;
        this.rotationYaw = rotationYaw;
    }
}
