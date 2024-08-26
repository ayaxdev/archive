package com.atani.nextgen.module.impl.combat;

import com.atani.nextgen.event.impl.ClickCallEvent;
import com.atani.nextgen.event.impl.UpdateEvent;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;

public class NoClickDelayModule extends ModuleFeature {

    public NoClickDelayModule() {
        super(new ModuleBuilder("NoClickDelay", "Completely removes the left click delay", ModuleCategory.COMBAT));
    }

    @EventHandler
    public final void onClicking(ClickCallEvent clickCallEvent) {
        clickCallEvent.leftClickCounter = 0;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
