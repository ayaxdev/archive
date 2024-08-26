package com.skidding.atlas.processor.impl.tracker;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.processor.Processor;
import io.github.racoondog.norbit.EventHandler;

public class PlayerTracker extends Processor {

    public int onGroundTicks, offGroundTicks;

    @EventHandler
    public final void onMotion(WalkingPacketsEvent walkingPacketsEvent) {
        if (walkingPacketsEvent.eventType.equals(Event.EventType.PRE)) {
            if (getPlayer().onGround) {
                onGroundTicks++;
                offGroundTicks = 0;
            } else {
                offGroundTicks++;
                onGroundTicks = 0;
            }
        }
    }

}
