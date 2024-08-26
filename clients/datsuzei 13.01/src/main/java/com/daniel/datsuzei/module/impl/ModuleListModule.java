package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.event.impl.Render2DEvent;
import com.daniel.datsuzei.font.FontManager;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.settings.impl.BooleanSetting;
import com.daniel.datsuzei.util.render.DrawUtil;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleListModule extends ModuleFeature {

    public final BooleanSetting font = new BooleanSetting("Font", true);

    public ModuleListModule() {
        super(new ModuleData("ModuleList", "Displays a list of enabled modules", ModuleCategory.RENDER),
                null, null);
    }

    @Listen
    public final Listener<Render2DEvent> render2DEventListener = render2DEvent -> {
        final FontRenderer font = this.font.getValue() ? FontManager.getSingleton().get("Arial", "Regular", 19) : mc.fontRendererObj;
        final ScaledResolution scaledResolution = render2DEvent.getScaledResolution();
        final List<ModuleFeature> modules = new ArrayList<>(ModuleManager.getSingleton().getFeatures());

        modules.sort((Comparator.comparingInt((ModuleFeature o) -> font.getStringWidth(o.getName()))).reversed());

        float y = 0;

        for(ModuleFeature module : modules) {
            if(!module.isEnabled())
                continue;

            final float textWidth = font.getStringWidth(module.getName());
            final float rectWidth = textWidth + 5;
            final float rectHeight = 15;

            DrawUtil.drawRectRelative(scaledResolution.getScaledWidth() - rectWidth, y, rectWidth, rectHeight, new Color(30, 30, 30, 80).getRGB());
            font.drawStringWithShadow(module.getName(), scaledResolution.getScaledWidth() - rectWidth / 2 - textWidth / 2, y + rectHeight / 2 - font.FONT_HEIGHT / 2f, -1);

            y += rectHeight;
        }
    };

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
