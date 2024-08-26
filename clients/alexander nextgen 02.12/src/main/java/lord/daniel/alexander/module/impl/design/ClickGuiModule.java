package lord.daniel.alexander.module.impl.design;

import io.github.nevalackin.radbus.Listen;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.ui.clickgui.ClickGuiScreen;
import lord.daniel.alexander.util.run.MultiThreadedUtil;
import org.lwjglx.input.Keyboard;

/**
 * Written by Daniel. on 25/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@CreateModule(name = "ClickGui", category = EnumModuleType.HUD, key = Keyboard.KEY_RSHIFT)
public class ClickGuiModule extends AbstractModule {

    public NumberValue<Float> x = new NumberValue<>("X", this, 50f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);
    public NumberValue<Float> y = new NumberValue<>("Y", this, 50f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);
    public NumberValue<Float> width = new NumberValue<>("Width", this, 495f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);
    public NumberValue<Float> height = new NumberValue<>("Height", this, 350f, 0f, Float.MAX_VALUE).addVisibleCondition(() -> false);

    private ClickGuiScreen clickGuiScreen;

    @Listen
    public final void onTick(RunTickEvent runTickEvent) {
        if(clickGuiScreen != null && runTickEvent.getStage() == Event.Stage.MID) {
            mc.displayGuiScreen(clickGuiScreen);
            setEnabled(false);
        }
    }

    @Override
    protected void onEnable() {
        if(clickGuiScreen == null) {
            MultiThreadedUtil.runAsync(() -> clickGuiScreen = new ClickGuiScreen());
        }
    }

    @Override
    protected void onDisable() {

    }

    @Override
    public String getSuffix() {
        return null;
    }

}
