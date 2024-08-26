package lord.daniel.alexander.module.impl.design;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;

/**
 * Written by Daniel. on 25/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@CreateModule(name = "Blur", category = EnumModuleType.HUD)
public class BlurModule extends AbstractModule {

    public final StringModeValue mode = new StringModeValue("Mode", this, "NewGaussian", new String[]{"NewGaussian", "OldGaussian"});
    public final NumberValue<Integer> offset = new NumberValue<>("Offset", this, 20, 1, 40).addVisibleCondition(mode, "OldGaussian");
    public final NumberValue<Integer> radius = new NumberValue<>("Radius", this, 25, 1, 25).addVisibleCondition(mode, "NewGaussian");
    public final NumberValue<Integer> sigma = new NumberValue<>("Sigma", this, 10, 1, 40).addVisibleCondition(mode, "NewGaussian");

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
