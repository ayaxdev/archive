package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "CorrectMovement", aliases = {"MovementCorrection", "MoveCorrection", "MovementFix", "MoveFix"}, categories = {EnumModuleType.MOVEMENT, EnumModuleType.COMBAT, EnumModuleType.PLAYER, EnumModuleType.INPUT})
public class CorrectMovement extends AbstractModule {

    public final BooleanValue silent = new BooleanValue("Silent", this, true);
    public final StringModeValue silentMode = new StringModeValue("SilentMode", this, "InputOverride", new String[]{"InputOverride", "Direct"});

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(silent.getValue() ? "Silent" : "Strict");
    };

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }
}
