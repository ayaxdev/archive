package com.skidding.atlas.event.impl.player.action;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;

@AllArgsConstructor
public class DropItemEvent extends Event {
    public final ItemStack droppedItem;
    public float rotationYaw, rotationPitch;
}
