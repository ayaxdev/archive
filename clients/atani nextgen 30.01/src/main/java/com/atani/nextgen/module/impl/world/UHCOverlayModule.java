package com.atani.nextgen.module.impl.world;

import com.atani.nextgen.event.impl.ItemRenderEvent;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.impl.SliderBuilder;
import io.github.racoondog.norbit.EventHandler;

public class UHCOverlayModule extends ModuleFeature {

    public final SettingFeature<Float> goldenNuggetScale = new SliderBuilder("GoldenNuggetScale", 2.5f, 0.3f, 5.0f, 1)
            .build();
    public final SettingFeature<Float> goldenAppleScale = new SliderBuilder("GoldenAppleScale", 2.5f, 0.3f, 5.0f, 1)
            .build();
    public final SettingFeature<Float> goldIngotScale = new SliderBuilder("GoldIngotScale", 2.0f, 0.3f, 5.0f, 1)
            .build();

    public UHCOverlayModule() {
        super(new ModuleBuilder("UHCOverlay", "Alter visibility of overpowered UHC items dropped on the ground", ModuleCategory.WORLD));
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
