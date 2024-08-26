package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.event.impl.PositionPacketEvent;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;
import org.lwjglx.input.Keyboard;

public class ToggleSprintModule extends ModuleFeature {

    public ToggleSprintModule() {
        super(new ModuleData("ToggleSprint", "Toggles sprint state", ModuleCategory.MOVEMENT),
                null, null);
    }

    @Listen
    public final Listener<PositionPacketEvent> positionPacketEventListener = _ -> mc.gameSettings.keyBindSprint.setPressed(true);

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
    }

}
