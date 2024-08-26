package com.skidding.atlas.event.impl.render.player;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RenderPitchHeadEvent extends Event {
    public float renderPitch, partialTicks;

    @AllArgsConstructor
    public static class HeadTurnDistanceEvent extends Event {

        public float yaw;

    }
}
