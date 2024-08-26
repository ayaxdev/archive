package com.atani.nextgen.module.impl.player;

import com.atani.nextgen.event.impl.UpdateEvent;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;

public class FastPlaceModule extends ModuleFeature {

    public FastPlaceModule() {
        super(new ModuleBuilder("FastPlace", "Alter the delay between block placements", ModuleCategory.PLAYER));
    }

    @EventHandler
    public final void onUpdate(UpdateEvent updateEvent) {
        mc.rightClickDelayTimer = 0;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        mc.rightClickDelayTimer = 6;
    }
}
