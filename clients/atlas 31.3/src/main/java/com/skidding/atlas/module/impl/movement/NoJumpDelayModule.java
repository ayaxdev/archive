package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.impl.player.movement.JumpTicksEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;

public class NoJumpDelayModule extends ModuleFeature {

    public NoJumpDelayModule() {
        super(new ModuleBuilder("NoJumpDelay", "Instantly jump without any delay", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public final void onDelay(JumpTicksEvent jumpTicksEvent) {
        jumpTicksEvent.jumpTicks = 0;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
