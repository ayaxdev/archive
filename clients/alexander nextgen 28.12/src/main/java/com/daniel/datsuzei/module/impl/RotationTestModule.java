package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.event.impl.RotationEvent;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.settings.impl.NumberSetting;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;

public class RotationTestModule extends ModuleFeature {

    public final NumberSetting<Float> yaw = new NumberSetting<>("Yaw", 100f, -180f, 360f);
    public final NumberSetting<Float> pitch = new NumberSetting<>("Pitch", 90f, -90f, 90f);

    public RotationTestModule() {
        super(new ModuleData("RotationTest", "Testing the rotation system", ModuleCategory.PLAYER),
                null, null);
    }

    @Listen
    public final Listener<RotationEvent> rotationEventListener = rotationEvent -> {
        rotationEvent.yaw = yaw.getValue();
        rotationEvent.pitch = pitch.getValue();
    };

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
