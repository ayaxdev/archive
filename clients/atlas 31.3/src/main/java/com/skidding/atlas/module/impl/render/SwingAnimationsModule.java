package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.event.impl.render.item.throwable.ArmSwingAnimationEvent;
import com.skidding.atlas.event.impl.render.item.hand.SmoothSwingAnimationEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;

public class SwingAnimationsModule extends ModuleFeature {

    public final SettingFeature<Float> swingSpeed = slider("Swing speed", 1, 0.1f, 3.5f, 1).build();
    public final SettingFeature<Boolean> smooth = check("Smooth", false).build();

    public SwingAnimationsModule() {
        super(new ModuleBuilder("SwingAnimations", "Adjusts swinging animations for a personalized experience", ModuleCategory.RENDER));
    }

    @EventHandler
    public final void onArmSwing(ArmSwingAnimationEvent speedModifierEvent) {
        speedModifierEvent.swingSpeed = swingSpeed.getValue();
    }

    @EventHandler
    public final void onItemSwing(SmoothSwingAnimationEvent smoothSwingAnimationEvent) {
        smoothSwingAnimationEvent.renderSwingProgress = smooth.getValue() ? 0 : smoothSwingAnimationEvent.renderSwingProgress;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
