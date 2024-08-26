package lord.daniel.alexander.module.impl.render;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "FullBright", enumModuleType = EnumModuleType.RENDER)
public class FullBrightModule extends AbstractModule {

    private float brightness = 0f;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        mc.gameSettings.gammaSetting = 1000;
    };

    @Override
    public void onEnable() {
        brightness = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = brightness;
    }
}
