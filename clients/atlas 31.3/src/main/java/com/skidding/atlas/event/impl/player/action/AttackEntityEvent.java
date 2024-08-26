package com.skidding.atlas.event.impl.player.action;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.entity.Entity;

@AllArgsConstructor
public class AttackEntityEvent extends Event {
    public Entity target;
}
