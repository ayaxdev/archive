package ja.tabio.argon.module.impl.hack.movement;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.PlayerUpdateEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.player.MovementUtil;

@ModuleData(name = "Fly", category = ModuleCategory.HACK)
@HackData(hackCategory = HackCategory.MOVEMENT)
public class FlyHack extends Module {

    public final ModeSetting mode = new ModeSetting("Mode", "Motion", "Motion");
    public final NumberSetting horizontalMotion = new NumberSetting("HorizontalMotion", 1, 0, 5, 1);
    public final NumberSetting verticalMotion = new NumberSetting("VerticalMotion", 1, 0, 5, 1);

    @EventHandler
    public final void onUpdate(PlayerUpdateEvent playerUpdateEvent) {
        if (mode.getValue().equalsIgnoreCase("Motion")) {
            if (MovementUtil.isMoving()) {
                MovementUtil.setSpeed(horizontalMotion.getValue());
            } else {
                MovementUtil.setSpeed(0);
            }

            if (mc.gameSettings.keyBindJump.pressed) {
                mc.thePlayer.motionY = verticalMotion.getValue();
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                mc.thePlayer.motionY = -verticalMotion.getValue();
            } else {
                mc.thePlayer.motionY = 0;
            }
        }
    }

}
