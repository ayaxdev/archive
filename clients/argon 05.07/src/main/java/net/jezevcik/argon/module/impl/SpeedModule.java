package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.ModeSetting;
import net.jezevcik.argon.config.setting.impl.number.DoubleSetting;
import net.jezevcik.argon.event.impl.ServerPlayerTickEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.player.MovementUtils;

public class SpeedModule extends Module {

    public final ModeSetting mode = new ModeSetting("Mode", "BHop", new String[]{"BHop", "On-Ground"}, this.config);
    public final DoubleSetting speed = new DoubleSetting("Speed", 1.5d, 1d, 4d, 0.1d, this.config);

    public SpeedModule() {
        super(ModuleParams.builder()
                .name("Speed")
                .category(ModuleCategory.MOVEMENT)
                .build());
    }

    @EventHandler
    public final void onUpdate(ServerPlayerTickEvent serverPlayerTickEvent) {
        if (!serverPlayerTickEvent.pre)
            return;

        if (!Minecraft.inGame())
            return;

        assert client.player != null;
        assert client.world != null;

        if (!MovementUtils.isMoving())
            return;

        switch (mode.getValue()) {
            case "BHop" -> {
                if (client.player.isOnGround()) {
                    MovementUtils.strafe();
                    client.player.jump();
                } else {
                    MovementUtils.setSpeed(speed.getValue() / 4d);
                }
            }

            case "On-Ground" -> {
                if (client.player.isOnGround())
                    MovementUtils.setSpeed(speed.getValue() / 4d);
            }
        }
    }

}
