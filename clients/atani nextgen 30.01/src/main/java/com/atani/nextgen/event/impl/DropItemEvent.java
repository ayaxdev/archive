package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
public class DropItemEvent extends Event {
    public final ItemStack droppedItem;
    public float rotationYaw, rotationPitch;
}
