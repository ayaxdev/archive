package net.jezevcik.argon.module.extension.impl;

import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.impl.ModeSetting;
import net.jezevcik.argon.config.setting.impl.number.DoubleSetting;
import net.jezevcik.argon.module.extension.Extension;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ReachExtension extends Extension {

    public final ModeSetting reachMode;
    public final DoubleSetting reach;

    public ReachExtension(final String name, final double value, final double min, final double max, final double step, final Config parent) {
        super(name, parent, false);

        reachMode = new ModeSetting(name + "Mode", "HeadToNearest", new String[]{"HeadToNearest", "HeadToHead"}, this.config);
        reach = new DoubleSetting(name, value, min, max, step, this.config);
    }

    public double distanceTo(final Entity entity) {
        return distanceTo(entity, null);
    }

    public double distanceTo(final Entity entity, final Vec3d precalculated) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game");

        assert client.player != null;
        assert client.world != null;

        if (reachMode.getValue().equals("HeadToHead")) {
            return client.player.distanceTo(entity);
        } else {
            if (precalculated == null)
                return PlayerUtils.getDistanceTo(entity);
            else
                return PlayerUtils.getDistanceTo(precalculated);
        }
    }

    public boolean valid(final Entity entity, final Vec3d precalculated) {
        return distanceTo(entity, precalculated) <= reach.getValue();
    }

    public boolean valid(final Entity entity) {
        return valid(entity, null);
    }

}
