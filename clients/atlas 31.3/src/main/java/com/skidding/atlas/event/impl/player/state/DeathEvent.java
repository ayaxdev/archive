package com.skidding.atlas.event.impl.player.state;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.util.DamageSource;

@AllArgsConstructor
public class DeathEvent extends Event {
    public final DamageSource damageSource;
    public float rotationYaw;
}
