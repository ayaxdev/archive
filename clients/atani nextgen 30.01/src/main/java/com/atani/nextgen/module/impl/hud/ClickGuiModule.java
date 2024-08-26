package com.atani.nextgen.module.impl.hud;

import com.atani.nextgen.module.ModuleCategory;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.screen.simple.SimpleClickGui;
import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.builder.impl.ModeBuilder;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends ModuleFeature {

    private GuiScreen guiScreen;

    public final SettingFeature<String> mode = new ModeBuilder("Mode", "The ClickGui that will be renderer", "Simple", new String[]{"Simple"})
            .addValueChangeListeners((setting, newValue, oldValue, pre) -> {
                if(pre) {
                    refreshGui(newValue);
                }
            })
            .build();

    public ClickGuiModule() {
        super(new ModuleBuilder("ClickGui", "A clicky little GUI", ModuleCategory.HUD).withKey(Keyboard.KEY_RSHIFT));
    }

    private void refreshGui(String mode) {
        // This is garbage code with only one clickgui, but it will be useful in the future
        switch (mode) {
            case "Simple" -> {
                guiScreen = new SimpleClickGui();
            }
        }
    }

    @Override
    protected void onEnable() {
        // This shouldn't happen, but just in case 🤷‍♀️
        if(guiScreen == null)
            refreshGui(mode.getValue());

        mc.displayGuiScreen(guiScreen);

        setEnabled(false);
    }

    @Override
    protected void onDisable() {

    }

}
