package com.atani.nextgen.module.impl.hud;

import com.atani.nextgen.event.impl.Render2DEvent;
import com.atani.nextgen.font.FontManager;
import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.processor.impl.tracker.ClickTracker;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;

public class HudTestModule extends ModuleFeature {

    public HudTestModule() {
        super(new ModuleBuilder("HudTest", "A testing module for HUD", ModuleCategory.HUD).withKey(Keyboard.KEY_O));
    }

    @EventHandler
    public final void onRender2D(Render2DEvent render2DEvent) {
        final FontRenderer fontRenderer = FontManager.getSingleton().get("Hack", 30);

        fontRenderer.drawStringWithShadow(ClickTracker.getLeftClicks() + " CPS", 1, 1, -1);
        fontRenderer.drawStringWithShadow("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 1, 2 + fontRenderer.FONT_HEIGHT, -1);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
