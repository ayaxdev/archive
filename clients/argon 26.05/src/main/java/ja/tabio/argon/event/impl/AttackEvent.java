package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AttackEvent extends Event {

    public final PlayerEntity player;
    public final Entity entity;

    public AttackEvent(PlayerEntity player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }
}
