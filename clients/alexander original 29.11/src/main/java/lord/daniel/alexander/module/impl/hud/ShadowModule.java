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
@ModuleData(name = "Shadow", aliases = {"Bloom", "AlphaBlur"}, enumModuleType = EnumModuleType.HUD)
public class ShadowModule extends AbstractModule {

    public final NumberValue<Integer> radius = new NumberValue<>("Radius", this,10, 0, 20);
    public final BooleanValue showIfDisplayIsInactive = new BooleanValue("ShowIfDisplayIsInactive", this, false);


    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
