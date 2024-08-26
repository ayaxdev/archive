package ja.tabio.argon.module.impl.hack.movement;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.EntityMovementEvent;
import ja.tabio.argon.event.impl.PlayerUpdateEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.player.MovementUtil;

@ModuleData(name = "Speed", category = ModuleCategory.HACK)
@HackData(hackCategory = HackCategory.MOVEMENT)
public class SpeedHack extends Module {

    public final ModeSetting mode = new ModeSetting("Mode", "Hop", "Hop", "OnGround");
    public final NumberSetting speed = new NumberSetting("Speed", 0.4f, 0f, 1.5f, 1);

    @EventHandler
    public final void onUpdate(PlayerUpdateEvent updateEvent) {
        if (updateEvent.type != PlayerType.LOCAL || updateEvent.stage != Stage.POST)
            return;

        if (!MovementUtil.isMoving())
            return;

        switch (mode.getValue()) {
            case "Hop" -> {
                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
            }
        }
    }

    @EventHandler
    public void onMove(final EntityMovementEvent movementEvent) {
        if (movementEvent.entity != mc.thePlayer)
            return;

        if (!MovementUtil.isMovingInput(false))
            return;

        switch (mode.getValue()) {
            case "OnGround", "Hop" -> {
                if (mode.getValue().equals("OnGround") && !mc.thePlayer.onGround)
                    break;

                final double[] motion = MovementUtil.Math.getMotion(speed.getValue());

                movementEvent.motionX = motion[0];
                movementEvent.motionZ = motion[1];
            }
        }
    }

}
