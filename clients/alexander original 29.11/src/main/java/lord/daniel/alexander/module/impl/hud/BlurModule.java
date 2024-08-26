package lord.daniel.alexander.module.impl.hud;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "Blur", enumModuleType = EnumModuleType.HUD)
public class BlurModule extends AbstractModule {

    public final NumberValue<Integer> radius = new NumberValue<>("Radius", this,20, 0, 50);
    public final NumberValue<Integer> sigma = new NumberValue<>("Sigma", this,12, 0, 25);
    public final BooleanValue showIfDisplayIsInactive = new BooleanValue("ShowIfDisplayIsInactive", this, false);

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
