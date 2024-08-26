package lord.daniel.alexander.module.impl.hud;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.color.ClientColorValue;
import lord.daniel.alexander.settings.impl.number.color.ColorValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;

import java.awt.*;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "Color", aliases = {"ColorManager", "ColorMixer"}, enumModuleType = EnumModuleType.HUD)
public class ColorModule extends AbstractModule {

    /*
    NumberValue<Integer> red = new NumberValue<>("Red", this, 178, 0, 255);
    NumberValue<Integer> green = new NumberValue<>("Green", this, 216, 0, 255);
    NumberValue<Integer> blue = new NumberValue<>("Blue", this, 236, 0, 255);
     */

    private final ClientColorValue colorValue = new ClientColorValue("Color", this, new Color(178, 216, 236), false, false);

    public static Color getClientColor() {
        ColorModule colorModule = ModuleStorage.getModuleStorage().getByClass(ColorModule.class);
        return colorModule.colorValue.getValue(1);
    }

    public static Color getClientColor(int counter) {
        ColorModule colorModule = ModuleStorage.getModuleStorage().getByClass(ColorModule.class);
        return colorModule.colorValue.getValue(counter);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}