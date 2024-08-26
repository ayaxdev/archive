package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.impl.input.mouse.RunClickEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;

public class NoClickDelayModule extends ModuleFeature {

    public NoClickDelayModule() {
        super(new ModuleBuilder("NoClickDelay", "Eliminates the delay between clicks", ModuleCategory.COMBAT));
    }

    @EventHandler
    public final void onClicking(RunClickEvent runClickEvent) {
        runClickEvent.leftClickCounter = 0;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
