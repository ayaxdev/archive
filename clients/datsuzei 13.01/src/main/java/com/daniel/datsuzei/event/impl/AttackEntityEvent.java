package com.daniel.datsuzei.event.impl;

import com.daniel.datsuzei.event.Event;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@RequiredArgsConstructor
public class AttackEntityEvent extends Event {
    public final EntityPlayer playerSp;
    public final Entity attackedEntity;
}
