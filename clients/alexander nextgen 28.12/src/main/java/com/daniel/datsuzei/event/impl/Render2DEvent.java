package com.daniel.datsuzei.event.impl;

import com.daniel.datsuzei.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@RequiredArgsConstructor
public class Render2DEvent extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;
}
