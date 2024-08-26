package lord.daniel.alexander.module.impl.options;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ColorValue;

import java.awt.*;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "FontRenderer", enumModuleType = EnumModuleType.OPTIONS)
public class FontRendererOptionModule extends AbstractModule {

    public final StringModeValue mode = new StringModeValue("Mode", this, "SmoothedOut", new String[]{"SmoothedOut", "Old"});
    public final NumberValue<Float> xShadowOffset = new NumberValue<>("XShadowOffset", this, 1f, 0f, 10f);
    public final NumberValue<Float> yShadowOffset = new NumberValue<>("YShadowOffset", this, 1f, 0f, 10f);
    public final BooleanValue coloredShadow = new BooleanValue("ColoredShadow", this, false);
    public final ColorValue shadowColour = new ColorValue("ShadowColour", this, new Color(50, 50, 50)).addVisibleCondition(coloredShadow, false);

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
