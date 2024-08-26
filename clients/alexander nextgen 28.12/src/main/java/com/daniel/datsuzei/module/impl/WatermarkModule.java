package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.event.impl.Render2DEvent;
import com.daniel.datsuzei.font.FontManager;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.settings.impl.BooleanSetting;
import com.daniel.datsuzei.settings.impl.ModeSetting;
import com.daniel.datsuzei.util.render.DrawUtil;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjglx.input.Keyboard;

import java.awt.*;

public class WatermarkModule extends ModuleFeature {

    public final ModeSetting displayMode = new ModeSetting("DisplayMode", "Normal", "Normal", "Classic", "Simple");
    public final BooleanSetting dropShadow = new BooleanSetting("DropShadow", true);

    public WatermarkModule() {
        super(new ModuleData("Watermark", "Displays a watermark", ModuleCategory.RENDER),
                new BindableData(Keyboard.KEY_K), null);
    }

    @Listen
    public final Listener<Render2DEvent> render2DEventListener = _ -> {
        switch (displayMode.getValue()) {
            case "Normal" -> {
                final FontRenderer fontRenderer = FontManager.getSingleton().get("Arial", "Regular", 50);
                if(dropShadow.getValue())
                    fontRenderer.drawStringWithShadow("Datsuzei", 3, 2, -1);
                else
                    fontRenderer.drawString("Datsuzei", 3, 2, -1);
            }
            case "Classic" -> {
                final String text = STR."\{DatsuzeiClient.CJ_NAME } \{DatsuzeiClient.VERSION} - \{Minecraft.getDebugFPS()} fps";
                DrawUtil.drawRectRelative(0, 0, mc.fontRendererObj.getStringWidth(text) + 2, mc.fontRendererObj.FONT_HEIGHT + 1, new Color(30, 30, 30, 80).getRGB());
                if(dropShadow.getValue())
                    mc.fontRendererObj.drawStringWithShadow(text, 1, 1, -1);
                else
                    mc.fontRendererObj.drawString(text, 1, 1, -1);
            }
            case "Simple" -> {
                GlStateManager.pushMatrix();
                GlStateManager.scale(3f, 3f, 3f);
                if(dropShadow.getValue())
                    mc.fontRendererObj.drawStringWithShadow("Datsuzei", 1, 1, -1);
                else
                    mc.fontRendererObj.drawString("Datsuzei", 1, 1, -1);
                GlStateManager.popMatrix();
            }
        }
    };

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
