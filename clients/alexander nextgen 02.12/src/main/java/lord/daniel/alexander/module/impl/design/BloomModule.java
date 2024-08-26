package lord.daniel.alexander.module.impl.design;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;

@CreateModule(name = "Bloom", displayNames = {"Shadow", "AlphaBlur"}, category = EnumModuleType.HUD)
public class BloomModule extends AbstractModule {

    public final NumberValue<Float> radius = new NumberValue<Float>("Radius", this, 7f, 0f, 20f, 0);

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public String getSuffix() {
        return null;
    }

}
