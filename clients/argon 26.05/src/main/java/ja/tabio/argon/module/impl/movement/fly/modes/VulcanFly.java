package ja.tabio.argon.module.impl.movement.fly.modes;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.TickEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.module.impl.movement.fly.Fly;
import ja.tabio.argon.utils.player.MovementUtils;

public class VulcanFly extends Extension {

    public VulcanFly(final String name, final Fly fly) {
        super(name, fly, false);
    }

    @EventHandler
    public final void onUpdate(TickEvent tickEvent) {
        if (tickEvent.stage == Stage.POST) {
            if (!Minecraft.inGame())
                return;

            assert mc.player != null;

            if (!mc.player.isOnGround() && !mc.player.isTouchingWater() && !mc.player.inPowderSnow && mc.player.fallDistance > 0) {
                if (mc.player.age % 2 == 0) {
                    MovementUtils.setMotionY(-0.155);
                } else {
                    MovementUtils.setMotionY(-0.1);
                }
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
