package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.GuiHandleEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import net.minecraft.client.gui.GuiChat;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "InventoryMovement", enumModuleType = EnumModuleType.MOVEMENT)
public class InventoryMoveModule extends AbstractModule {

    public final BooleanValue jump = new BooleanValue("Jump", this, true);
    public final BooleanValue sprint = new BooleanValue("Sprint", this, true);

    @EventLink
    public final Listener<GuiHandleEvent> guiHandleEventListener = guiHandleEvent -> {
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen == null) {
            return;
        }

        handleMovement();
    };


    public void handleMovement() {
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindLeft.pressed = isKeyDown(getGameSettings().keyBindLeft.getKeyCode());
        getGameSettings().keyBindRight.pressed = isKeyDown(getGameSettings().keyBindRight.getKeyCode());
        if (jump.getValue())
            getGameSettings().keyBindJump.pressed = isKeyDown(getGameSettings().keyBindJump.getKeyCode());
        if (sprint.getValue())
            getGameSettings().keyBindSprint.pressed = isKeyDown(getGameSettings().keyBindSprint.getKeyCode());
    }


    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
