package ja.tabio.argon.module.impl.movement.fly.modes;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.PlayerTickEvent;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.module.impl.movement.fly.Fly;

public class AbilitiesFly extends Extension {

    public AbilitiesFly(final String name, final Fly fly) {
        super(name, fly, false);
    }

    @EventHandler
    public final void onTick(PlayerTickEvent playerTickEvent) {
        assert mc.player != null;
        assert mc.world != null;

        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().allowFlying = true;
    }

    @Override
    public void onEnable() {
        if (!Minecraft.inGame())
            return;

        assert mc.player != null;
        assert mc.world != null;

        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().allowFlying = true;
    }

    @Override
    public void onDisable() {
        if (!Minecraft.inGame())
            return;

        assert mc.player != null;
        assert mc.world != null;

        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().setFlySpeed(0.05f);

        if (!mc.player.getAbilities().creativeMode)
            mc.player.getAbilities().allowFlying = false;
    }

    @Override
    public String toString() {
        return name;
    }
}
