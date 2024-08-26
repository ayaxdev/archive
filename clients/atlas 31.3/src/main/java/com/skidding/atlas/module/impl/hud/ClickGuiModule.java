package com.skidding.atlas.module.impl.hud;

import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.screen.simple.SimpleFrameUI;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.impl.ModeBuilder;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends ModuleFeature {

    private GuiScreen guiScreen;

    public final SettingFeature<String> mode = new ModeBuilder("Mode", "Simple", new String[]{"Simple"})
            .addValueChangeListeners((_, newValue, _, pre) -> {
                if (pre) {
                    refreshGui(newValue);
                }
            })
            .build();

    public final SettingFeature<Float> animationDuration = slider("Opening animation duration", 250, 0, 1000, 0)
            .addDependency(mode, "Simple")
            .build();
    public final SettingFeature<Integer> enabledColor = color("Enabled color", 0, 159, 216, 100)
            .addDependency(mode, "Simple")
            .build();

    public ClickGuiModule() {
        super(new ModuleBuilder("ClickGui", "Shows a GUI allowing users to modify modules easily", ModuleCategory.HUD).withKey(Keyboard.KEY_RSHIFT));
    }

    private void refreshGui(String mode) {
        // This is garbage code with only one clickgui, but it will be useful in the future
        switch (mode) {
            case "Simple" -> {
                guiScreen = new SimpleFrameUI();
            }
        }
    }

    @Override
    protected void onEnable() {
        // This shouldn't happen, but just in case 🤷‍♀️
        if (guiScreen == null)
            refreshGui(mode.getValue());

        mc.displayGuiScreen(guiScreen);

        setEnabled(false);
    }

    @Override
    protected void onDisable() {

    }

    public int getEnabledRed() {
        return enabledColor.getValue() >> 16 & 0xFF;
    }

    public int getEnabledGreen() {
        return enabledColor.getValue() >> 8 & 0xFF;
    }

    public int getEnabledBlue() {
        return enabledColor.getValue() & 0xFF;
    }

    public int getEnabledAlpha() {
        return enabledColor.getValue() >> 24 & 0xFF;
    }

}
