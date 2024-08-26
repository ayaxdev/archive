package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.impl.client.BackgroundEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.RotationProcessor;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.java.object.EnumUtil;
import io.github.racoondog.norbit.EventHandler;

public final class CorrectMovementModule extends ModuleFeature {

    public final SettingFeature<String> movementFix = mode("Movement Fix", RotationProcessor.MovementFix.NONE.toString(), RotationProcessor.MovementFix.values()).build();

    final RotationProcessor rotationProcessor = ProcessorManager.getSingleton().getByClass(RotationProcessor.class);

    public CorrectMovementModule() {
        super(new ModuleBuilder("CorrectMovement", "Angles your movement to the server-side rotations", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public void onBackground(BackgroundEvent backgroundEvent) {
        rotationProcessor.movementFix = EnumUtil.getEnumConstantBasedOnString(RotationProcessor.MovementFix.class, movementFix.getValue());
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        rotationProcessor.movementFix = RotationProcessor.MovementFix.NONE;
    }

}
