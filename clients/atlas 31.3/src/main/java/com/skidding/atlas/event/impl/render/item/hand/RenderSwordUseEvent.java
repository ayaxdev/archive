package com.skidding.atlas.event.impl.render.item.hand;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.ItemRenderer;

@AllArgsConstructor
public class RenderSwordUseEvent extends Event {
    public ItemRenderer itemRenderer;
    public float equippedProgress, renderSwingProgress, realSwingProgress;

}
