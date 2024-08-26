package com.skidding.atlas.module.impl.hud;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.event.impl.game.RunTickEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import io.github.racoondog.norbit.EventHandler;

public final class PostProcessingModule extends ModuleFeature {

    public final SettingFeature<Boolean> blur = check("Blur", true).build();
    public final SettingFeature<String> blurQuality = mode("Blur mode", "New", new String[]{"Old", "New"}).addDependency(blur).build();
    public final SettingFeature<Float> blurRadius = slider("Blur radius", 25, 1, 25, 1).addDependency(blur).addDependency(blurQuality, "New").build();
    public final SettingFeature<Float> blurSigma = slider("Blur offset", 10, 1, 40, 1).addDependency(blur).addDependency(blurQuality, "New").build();
    public final SettingFeature<Float> blurRange = slider("Blur range", 25, 1, 25, 1).addDependency(blur).addDependency(blurQuality, "Old").build();

    public final SettingFeature<Boolean> shadow = check("Shadow", true).build();
    public final SettingFeature<Float> shadowIterations = slider("Shadow iterations", 3, 1, 8, 1).addDependency(shadow).build();
    public final SettingFeature<Float> shadowOffset = slider("Shadow offset", 1, 1, 10, 1).addDependency(shadow).build();

    private boolean initialized = false;

    public PostProcessingModule() {
        super(new ModuleBuilder("PostProcessing", "Allows you to enable post-processing effects", ModuleCategory.HUD)
                .withEvents(false));

        AtlasClient.getInstance().eventPubSub.subscribe(this);

        setEnabled(true);
    }

    @EventHandler
    public void onTick(RunTickEvent tickEvent) {
        if (AtlasClient.getInstance().launched && !initialized) {
            ShaderRenderer.INSTANCE.init();

            initialized = true;
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
