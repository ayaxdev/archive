package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent extends Event {
    public final Entity entity;
    public boolean sync = true,
            swing = true;

    public AttackEvent(Entity entity) {
        this.entity = entity;
    }
}
