package ja.tabio.argon.module.impl.movement;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.HandleKeyEvent;
import ja.tabio.argon.event.impl.PlayerTickEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.setting.impl.BooleanSetting;

@RegisterModule
public class AirJump extends Module {

    private int level;

    public final BooleanSetting maintainY = new BooleanSetting("MaintainY", false);

    public AirJump() {
        super(ModuleParams.builder()
                .name("AirJump")
                .category(ModuleCategory.MOVEMENT)
                .build());
    }

    @Override
    public boolean onEnable() {
        if (!Minecraft.inGame())
            return false;

        assert mc.player != null;

        level = mc.player.getBlockPos().getY();

        return true;
    }

    @EventHandler
    public final void onKey(HandleKeyEvent keyEvent) {
        if (!Minecraft.inGame())
            return;

        assert mc.player != null;

        if (mc.currentScreen != null || mc.player.isOnGround())
            return;

        if (keyEvent.action != HandleKeyEvent.KeyAction.PRESS) return;

        if (mc.options.jumpKey.matchesKey(keyEvent.key, 0)) {
            level = mc.player.getBlockPos().getY();
            mc.player.jump();
        }
        else if (mc.options.sneakKey.matchesKey(keyEvent.key, 0)) {
            level--;
        }
    }

    @EventHandler
    public final void onTick(PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.stage != Stage.POST || playerTickEvent.type != PlayerType.LOCAL) {
            return;
        }

        assert mc.player != null;

        if (maintainY.getValue() && mc.player.getBlockPos().getY() == level && mc.options.jumpKey.isPressed()) {
            mc.player.jump();
        }
    }

}
