package com.skidding.atlas.processor.impl;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.hud.HUDManager;
import com.skidding.atlas.event.impl.render.overlay.Render2DEvent;
import com.skidding.atlas.processor.Processor;
import com.skidding.atlas.screen.draggable.DesignerUI;
import io.github.racoondog.norbit.EventHandler;

import java.util.Comparator;

public final class HUDProcessor extends Processor {

    @EventHandler(priority = -9999)
    public void onRender2D(Render2DEvent render2DEvent) {
        if(render2DEvent.eventType != Event.EventType.POST)
            return;

        HUDManager.getSingleton().renderElements.sort(Comparator.comparing(hudElement -> hudElement.drag ? Integer.MIN_VALUE : -hudElement.priority));
        HUDManager.getSingleton().drawElements(mc.currentScreen instanceof DesignerUI);
    }

}
