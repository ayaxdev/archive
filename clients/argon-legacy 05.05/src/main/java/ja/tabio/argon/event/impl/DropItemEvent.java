package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class DropItemEvent extends Event {
    public final Entity entity;
    public final ItemStack droppedItem;
    public float rotationYaw, rotationPitch;

    public DropItemEvent(Entity entity, ItemStack droppedItem, float rotationYaw, float rotationPitch) {
        this.entity = entity;
        this.droppedItem = droppedItem;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
