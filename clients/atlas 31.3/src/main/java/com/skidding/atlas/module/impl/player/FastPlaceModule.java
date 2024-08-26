package com.skidding.atlas.module.impl.player;

import com.skidding.atlas.event.impl.player.update.UpdateEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;

public class FastPlaceModule extends ModuleFeature {

    public FastPlaceModule() {
        super(new ModuleBuilder("FastPlace", "Eliminates delays between block placements for quicker building", ModuleCategory.PLAYER));
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
