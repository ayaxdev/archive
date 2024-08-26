package com.skidding.atlas.event.impl.render.world;

import com.skidding.atlas.event.Event;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Render3DEvent extends Event {
    public final float partialTicks;
}
