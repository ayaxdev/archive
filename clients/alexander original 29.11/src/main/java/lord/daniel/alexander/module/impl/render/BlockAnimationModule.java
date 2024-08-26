package lord.daniel.alexander.module.impl.render;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "BlockAnimation", categories = EnumModuleType.RENDER)
public class BlockAnimationModule extends AbstractModule {

    public final StringModeValue animation = new StringModeValue("Animation", this, "1.7", new String[]{
            "1.7", "Skidding", "Exhibition", "Spin", "Forward", "Shove", "Chill", "Butter", "Smooth", "Slide", "Short", "Push",
            "Small", "Dortware", "Stab", "Dortware 2", "Rise", "Flush", "Whack", "Big Whack", "Wobble", "Chungus", "Bitch Slap",
            "Swong", "Spinny", "Reverse", "Down", "Rhys", "Throw", "Inwards", "Cockless", "Swang", "LB", "Exhibition 2", "Old", "Leaked"});

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
