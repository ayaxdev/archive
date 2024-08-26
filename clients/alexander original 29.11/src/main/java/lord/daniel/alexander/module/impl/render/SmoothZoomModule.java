package lord.daniel.alexander.module.impl.render;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "SmoothZoom", enumModuleType = EnumModuleType.RENDER)
public class SmoothZoomModule extends AbstractModule {
    public NumberValue<Double> multiplier = new NumberValue<>("Multiplier", this, 0.15D, 0.01D, 1D, 2);

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
