package com.atani.nextgen.module.impl.hud;

import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;

public class WatermarkModule extends ModuleFeature {

    public WatermarkModule() {
        super(new ModuleBuilder("Watermark", "A cutesy watermark", ModuleCategory.HUD));
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
