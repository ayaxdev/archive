package com.skidding.atlas.event.impl.render.item.hand;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SmoothSwingAnimationEvent extends Event {
    public float renderSwingProgress;
}
