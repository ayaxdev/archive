package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;

@RequiredArgsConstructor
public class Render3DEvent extends Event {
    public final float partialTicks;
}
