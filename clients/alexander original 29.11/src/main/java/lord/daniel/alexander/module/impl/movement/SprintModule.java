package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.module.impl.world.ScaffoldWalkModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import net.minecraft.client.settings.KeyBinding;

@ModuleData(name = "Sprint", enumModuleType = EnumModuleType.MOVEMENT)
public class SprintModule extends AbstractModule {
    private ScaffoldWalkModule scaffoldWalkModule;

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {

        if (scaffoldWalkModule == null) {
            scaffoldWalkModule = ModuleStorage.getModuleStorage().getByClass(ScaffoldWalkModule.class);
        }

        if (scaffoldWalkModule.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            mc.thePlayer.setSprinting(false);
            return;
        }
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
