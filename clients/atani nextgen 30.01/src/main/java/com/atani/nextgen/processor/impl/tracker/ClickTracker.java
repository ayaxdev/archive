package com.atani.nextgen.processor.impl.tracker;

import com.atani.nextgen.event.Event;
import com.atani.nextgen.event.impl.MouseClickRegisterEvent;
import com.atani.nextgen.processor.Processor;
import io.github.racoondog.norbit.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ClickTracker extends Processor {

    private static final List<Long> leftClicks = new ArrayList<Long>();

    private static final List<Long> rightClicks = new ArrayList<Long>();


    @EventHandler
    public final void onMouse(MouseClickRegisterEvent mouseClickRegisterEvent) {
        if(mouseClickRegisterEvent.button == 0 && mouseClickRegisterEvent.eventType == Event.EventType.MID) {
            leftClicks.add(System.currentTimeMillis());
        } else if(mouseClickRegisterEvent.button == 1 && mouseClickRegisterEvent.eventType == Event.EventType.MID) {
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
