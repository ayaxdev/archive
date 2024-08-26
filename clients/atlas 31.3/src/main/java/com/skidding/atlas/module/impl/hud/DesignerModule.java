package com.skidding.atlas.module.impl.hud;

import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.screen.draggable.DesignerUI;
import org.lwjgl.input.Keyboard;

public class DesignerModule extends ModuleFeature {

    public DesignerModule() {
        super(new ModuleBuilder("Designer", "Shows a GUI that allows the user to modify the client's HUD", ModuleCategory.HUD)
                .withKey(Keyboard.KEY_INSERT));
    }

    private DesignerUI designerUI;

    @Override
    protected void onEnable() {
        if(designerUI == null)
            designerUI = new DesignerUI();

        mc.displayGuiScreen(designerUI);

        setEnabled(false);
    }

    @Override
    protected void onDisable() {

    }
}
