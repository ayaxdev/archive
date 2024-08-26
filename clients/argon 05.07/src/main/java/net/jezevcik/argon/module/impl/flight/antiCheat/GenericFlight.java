package net.jezevcik.argon.module.impl.flight.antiCheat;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.config.setting.impl.ModeSetting;
import net.jezevcik.argon.config.setting.impl.number.DoubleSetting;
import net.jezevcik.argon.config.setting.impl.number.IntSetting;
import net.jezevcik.argon.event.impl.ServerPlayerTickEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.choice.Choice;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.objects.SupplierFactory;
import net.jezevcik.argon.utils.player.MovementUtils;

public class GenericFlight extends Choice {

    public final ModeSetting genericMethod = new ModeSetting("GenericMethod", "Method", "Motion", new String[]{"Motion", "Teleport"}, this.config);
    public final DoubleSetting speed = new DoubleSetting("GenericSpeed", "Speed", 2, 0, 5, 0.1d, this.config);

    public final IntSetting teleportDelay = new IntSetting("TeleportDelay", 2, 1, 10, 1, this.config)
            .visibility(SupplierFactory.setting(genericMethod, true, "Teleport"));
    public final BooleanSetting strafeBetweenTeleports = new BooleanSetting("StrafeBetweenTeleports", true, this.config)
            .visibility(SupplierFactory.setting(genericMethod, true, "Teleport"));

    private int lastTeleportTick = 0;

    public GenericFlight(String name, Module parent) {
        super(name, parent);
    }

    @EventHandler
    public final void onServer(ServerPlayerTickEvent serverPlayerTickEvent) {
        if (!serverPlayerTickEvent.pre)
            return;

        if (!Minecraft.inGame())
            return;

        switch (genericMethod.getValue()) {
            case "Motion" -> {
                final double speed = this.speed.getValue() / 2D;

                if (client.options.jumpKey.isPressed()) {
                    MovementUtils.setMotionY(speed);
                } else if (client.options.sneakKey.isPressed()) {
                    MovementUtils.setMotionY(-speed);
                } else {
                    MovementUtils.setMotionY(0);
                }

                if (MovementUtils.isMoving())
                    MovementUtils.setSpeed(speed);
                else
                    MovementUtils.setSpeed(0);
            }

            case "Teleport" -> {
                final double[] velocity = MovementUtils.Math.forward(MovementUtils.Math.getStrafeYaw(client.player.getYaw()
                        , client.player.forwardSpeed
                        , client.player.sidewaysSpeed));

                final double forwardX = client.player.getX() + velocity[0] * speed.getValue()
                        , teleportY = client.player.getY() + (client.options.jumpKey.isPressed() ? speed.getValue() : client.options.sneakKey.isPressed() ? -speed.getValue() : 0)
                        , forwardZ = client.player.getZ() + velocity[1] * speed.getValue();

                lastTeleportTick = Math.min(lastTeleportTick, client.player.age);

                if (client.player.age - lastTeleportTick > teleportDelay.getValue()) {
                    MovementUtils.setSpeed(0);

                    if (MovementUtils.isMoving())
                        client.player.setPos(forwardX, teleportY, forwardZ);
                    else if (teleportY != client.player.getY())
                        client.player.setPos(client.player.getX(), teleportY, client.player.getZ());

                    lastTeleportTick = client.player.age;
                } else {
                    if (MovementUtils.isMoving() && strafeBetweenTeleports.getValue()) {
                        MovementUtils.strafe();
                    } else {
                        MovementUtils.setSpeed(0);
                    }
                }

                MovementUtils.setMotionY(0);
            }
        }
    }


}
