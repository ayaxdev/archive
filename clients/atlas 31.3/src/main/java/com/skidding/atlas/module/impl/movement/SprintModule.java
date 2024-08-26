package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.impl.player.movement.DirectionalSprintCheckEvent;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.settings.KeyBinding;

public class SprintModule extends ModuleFeature {

    public final SettingFeature<Boolean> allDirections = check("All directions", false).build();

    public SprintModule() {
        super(new ModuleBuilder("Sprint", "Automatically activates sprinting mode while moving", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public final void onPlayerPackets(WalkingPacketsEvent walkingPacketsEvent) {
        mc.gameSettings.keyBindSprint.pressed = true;
    }

    @EventHandler
    public final void onSprintCheck(DirectionalSprintCheckEvent sprintCheckEvent) {
        if (allDirections.getValue() && MovementUtil.INSTANCE.getSpeed() != 0) {
            sprintCheckEvent.directionCheck = false;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }
}
