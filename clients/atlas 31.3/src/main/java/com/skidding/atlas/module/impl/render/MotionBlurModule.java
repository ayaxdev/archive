package com.skidding.atlas.module.impl.render;

import com.skidding.atlas.event.impl.render.overlay.Render2DEvent;
import com.skidding.atlas.event.impl.render.world.Render3DEvent;
import com.skidding.atlas.event.impl.world.RenderWorldPassEvent;
import com.skidding.atlas.event.impl.game.window.ResolutionChangeEvent;
import com.skidding.atlas.misc.blur.MonkeyBlur;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;

public final class MotionBlurModule extends ModuleFeature {

    public final SettingFeature<Float> blurStrength = slider("Strength", 2, 1, 10, 0)
            .build();

    public final MonkeyBlur monkeyBlur = new MonkeyBlur(blurStrength::getValue);

    public MotionBlurModule() {
        super(new ModuleBuilder("MotionBlur", "Adds dynamic motion blur effects to in-game movements", ModuleCategory.RENDER));
    }

    @EventHandler
    public void onRenderWorldPass(RenderWorldPassEvent renderWorldPassEvent) {
        switch (renderWorldPassEvent.eventType) {
            case PRE -> {
                monkeyBlur.startFrame();
            }
            case MID -> {
                monkeyBlur.setupCamera(renderWorldPassEvent.partialTicks);
            }

            default -> {}
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent render3DEvent) {
        monkeyBlur.endFrame();
    }

    @EventHandler
    public void onRes(ResolutionChangeEvent resolutionChangeEvent) {
        monkeyBlur.onResolutionChange();
    }

    @EventHandler
    public void on2D(Render2DEvent render2DEvent) {

    }


    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
    }
}
