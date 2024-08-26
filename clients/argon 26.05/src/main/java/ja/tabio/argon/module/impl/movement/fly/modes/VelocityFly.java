package ja.tabio.argon.module.impl.movement.fly.modes;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.PlayerTickEvent;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.module.impl.movement.fly.Fly;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.utils.player.MovementUtils;
import net.minecraft.util.math.Vec3d;

public class VelocityFly extends Extension {

    public final NumberSetting speed = new NumberSetting("Speed", 1, 0, 5, 1);

    public VelocityFly(final String name, final Fly fly) {
        super(name, fly, false);
    }

    @EventHandler
    public final void onTick(PlayerTickEvent playerTickEvent) {
        assert mc.player != null;
        assert mc.world != null;

        if (playerTickEvent.stage == Stage.POST && playerTickEvent.type == PlayerType.LOCAL) {
            mc.player.getAbilities().flying = false;
            mc.player.setVelocity(0, 0, 0);

            if (MovementUtils.isMoving()) {
                MovementUtils.setSpeed(speed.getValue());
            }

            Vec3d playerVelocity = mc.player.getVelocity();

            if (mc.options.jumpKey.isPressed())
                playerVelocity = playerVelocity.add(0, speed.getValue(), 0);
            if (mc.options.sneakKey.isPressed())
                playerVelocity = playerVelocity.subtract(0, speed.getValue(), 0);

            mc.player.setVelocity(playerVelocity);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
