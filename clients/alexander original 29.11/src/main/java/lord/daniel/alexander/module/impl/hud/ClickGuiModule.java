package lord.daniel.alexander.module.impl.hud;

import lord.daniel.alexander.clickgui.ClickGuiScreen;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import org.lwjgl.input.Keyboard;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "ClickGui", enumModuleType = EnumModuleType.HUD, key = Keyboard.KEY_RSHIFT)
public class ClickGuiModule extends AbstractModule {

    public NumberValue<Float> x = new NumberValue<>("X", this, 50f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);
    public NumberValue<Float> y = new NumberValue<>("Y", this, 50f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);
    public NumberValue<Float> width = new NumberValue<>("Width", this, 608f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);
    public NumberValue<Float> height = new NumberValue<>("Height", this, 300f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);

    public ClickGuiScreen clickGuiScreen;

    @Override
    public void onEnable() {
        if(clickGuiScreen == null)
            clickGuiScreen = new ClickGuiScreen();
        mc.displayGuiScreen(clickGuiScreen);
        setEnabled(false);
    }

    @Override
    public void onDisable() {

    }
}