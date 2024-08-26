package com.skidding.atlas.processor.impl.tracker;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.input.mouse.RegisterClickEvent;
import com.skidding.atlas.processor.Processor;
import io.github.racoondog.norbit.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ClickTracker extends Processor {

    private static final List<Long> leftClicks = new ArrayList<Long>();

    private static final List<Long> rightClicks = new ArrayList<Long>();


    @EventHandler
    public final void onMouse(RegisterClickEvent registerClickEvent) {
        if(registerClickEvent.button == 0 && registerClickEvent.eventType == Event.EventType.MID) {
            leftClicks.add(System.currentTimeMillis());
        } else if(registerClickEvent.button == 1 && registerClickEvent.eventType == Event.EventType.MID) {
            rightClicks.add(System.currentTimeMillis());
        }
    }

    public static int getLeftClicks() {
        final long time = System.currentTimeMillis();
        leftClicks.removeIf(aLong -> aLong + 1000 < time);
        return leftClicks.size();
    }

    public static int getRightClicks() {
        final long time = System.currentTimeMillis();
        rightClicks.removeIf(aLong -> aLong + 1000 < time);
        return rightClicks.size();
    }

}
