package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "NoClickDelay", categories = {EnumModuleType.COMBAT, EnumModuleType.GHOST})
public class NoClickDelay extends AbstractModule {

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        mc.leftClickCounter = 0;
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
