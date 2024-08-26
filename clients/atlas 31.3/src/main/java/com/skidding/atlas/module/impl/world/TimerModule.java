package com.skidding.atlas.module.impl.world;

import com.skidding.atlas.event.impl.player.update.UpdateEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;

public class TimerModule extends ModuleFeature {

    public final SettingFeature<Float> gameSpeed = slider("Game speed", 1, 0.1f, 10, 1).build();

    public TimerModule() {
        super(new ModuleBuilder("Timer", "Adjusts the game speed", ModuleCategory.WORLD));
    }

    @EventHandler
    public final void onUpdate(UpdateEvent updateEvent) {
        mc.timer.timerSpeed = gameSpeed.getValue();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        mc.timer.timerSpeed = 1.0f;
    }

}
