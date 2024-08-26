package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;

@AllArgsConstructor
public class ItemRenderEvent extends Event {
    public Item item;
    public float scale, rotationX, rotationY, rotationZ;
    public boolean shouldScale;

    public ItemRenderEvent(Item item, float rotationX, float rotationY, float rotationZ) {
        this.item = item;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }
}