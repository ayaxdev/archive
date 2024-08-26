package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import io.github.racoondog.norbit.EventHandler;

public class FastStopModule extends ModuleFeature {

    public final SettingFeature<Boolean> groundOnly = check("Ground only", true).build();

    public FastStopModule() {
        super(new ModuleBuilder("FastStop", "Enables quick halting of movement velocity", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public final void onMotion(WalkingPacketsEvent walkingPacketsEvent) {
        if (!MovementUtil.INSTANCE.isMoving() && (!groundOnly.getValue() || walkingPacketsEvent.onGround)) {
            MovementUtil.INSTANCE.stop();
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
