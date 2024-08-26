package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class DeathEvent extends Event {
    public final Entity entity;
    public final DamageSource damageSource;
    public float rotationYaw;

    public DeathEvent(Entity entity, DamageSource damageSource, float rotationYaw) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.rotationYaw = rotationYaw;
    }
}
