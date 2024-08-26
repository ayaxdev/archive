package com.skidding.atlas.event.impl.player.misc;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.entity.Entity;

@AllArgsConstructor
public class CollisionBoxEvent extends Event {
    public Entity entity;
    public float size;
}
