package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RightClickDelayEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */

@ModuleData(name = "RightClickDelay", categories = {EnumModuleType.PLAYER, EnumModuleType.INPUT, EnumModuleType.WORLD, EnumModuleType.GHOST})
public class RightClickDelayModule extends AbstractModule {

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", this, 0, 0, 4);

    @EventLink
    public final Listener<RightClickDelayEvent> rightClickDelayEventListener = rightClickDelayEvent -> {
        rightClickDelayEvent.setDelay(Math.min(rightClickDelayEvent.getDelay(), delay.getValue()));
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
