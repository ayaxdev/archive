package com.atani.nextgen.event.impl;

import com.atani.nextgen.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RenderPitchHeadEvent extends Event {
    public float renderPitch, partialTicks;
}
