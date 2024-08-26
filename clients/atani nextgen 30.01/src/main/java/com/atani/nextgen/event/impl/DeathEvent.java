package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.DamageSource;

@AllArgsConstructor
public class DeathEvent extends Event {
    public final DamageSource damageSource;
    public float rotationYaw;
}
