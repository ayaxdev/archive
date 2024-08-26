package com.skidding.atlas.module.impl.world;

import com.skidding.atlas.event.impl.render.item.throwable.ItemRenderEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import io.github.racoondog.norbit.EventHandler;

public class UHCOverlayModule extends ModuleFeature {

    public final SettingFeature<Float> goldenNuggetScale = slider("Golden nugget scale", 2.5f, 0.3f, 5.0f, 1).build();
    public final SettingFeature<Float> goldenAppleScale = slider("Golden apple scale", 2.5f, 0.3f, 5.0f, 1).build();
    public final SettingFeature<Float> goldIngotScale = slider("Gold ingot scale", 2.0f, 0.3f, 5.0f, 1).build();

    public UHCOverlayModule() {
        super(new ModuleBuilder("UHCOverlay", "Alter visibility of UHC items that are dropped", ModuleCategory.WORLD));
    }

    @EventHandler
    public final void onItemRendered(ItemRenderEvent itemRenderEvent) {
        switch (itemRenderEvent.item.getUnlocalizedName()) {
            case "item.appleGold":
                itemRenderEvent.shouldScale = true;
                itemRenderEvent.scale = goldenAppleScale.getValue();
                itemRenderEvent.rotationY += 0.12f;
                break;
            case "item.goldNugget":
                itemRenderEvent.shouldScale = true;
                itemRenderEvent.scale = goldenNuggetScale.getValue();
                itemRenderEvent.rotationY += 0.12f;
                break;
            case "item.ingotGold":
                itemRenderEvent.shouldScale = true;
                itemRenderEvent.scale = goldIngotScale.getValue();
                itemRenderEvent.rotationY += 0.12f;
                break;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
