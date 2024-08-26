package com.skidding.atlas.event.impl.render.overlay;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShowDebugInfoEvent extends Event {
    public boolean showDebug;
}
