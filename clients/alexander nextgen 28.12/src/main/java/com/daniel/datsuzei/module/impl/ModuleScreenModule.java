package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.screen.clickgui.ClickGui;
import com.daniel.datsuzei.settings.impl.NumberSetting;
import org.lwjglx.input.Keyboard;

public class ModuleScreenModule extends ModuleFeature {

    public NumberSetting<Integer> red = new NumberSetting<>("Red", 0, 0, 255);
    public NumberSetting<Integer> green = new NumberSetting<>("Green", 153, 0, 255);
    public NumberSetting<Integer> blue =  new NumberSetting<>("Blue", 153, 0, 255);

    public ClickGui clickGui = new ClickGui(this);

    public ModuleScreenModule() {
        super(new ModuleData("ModuleScreen", "A screen for toggling and configuring modules", ModuleCategory.RENDER),
                new BindableData(Keyboard.KEY_RSHIFT), null);
    }

    @Override
    protected void onEnable() {
        mc.displayGuiScreen(clickGui);
        setEnabled(false);
    }

    @Override
    protected void onDisable() {

    }

}
