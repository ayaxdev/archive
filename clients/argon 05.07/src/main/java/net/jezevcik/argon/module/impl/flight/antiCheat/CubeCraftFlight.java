package net.jezevcik.argon.module.impl.flight.antiCheat;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.config.setting.impl.number.DoubleSetting;
import net.jezevcik.argon.config.setting.impl.number.IntSetting;
import net.jezevcik.argon.event.impl.MovementEvent;
import net.jezevcik.argon.event.impl.TickEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.choice.Choice;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.objects.SupplierFactory;
import net.jezevcik.argon.utils.player.MovementUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class CubeCraftFlight extends Choice {

    public final DoubleSetting horizontalSpeed = new DoubleSetting("HorizontalSpeed", 3.5d, 0.1d, 10d, 0.1d, this.config);
    public final BooleanSetting constantSpeed = new BooleanSetting("ConstantSpeed", false, this.config);
    public final DoubleSetting verticalSpeed = new DoubleSetting("VerticalSpeed", 0.7d, 0.1d, 1d, 0.1d, this.config);
    public final BooleanSetting selfBoost = new BooleanSetting("SelfBoost", false, this.config);
    public final IntSetting boostTicks = new IntSetting("BoostTicks", 30, 10, 50, 1, this.config)
            .visibility(SupplierFactory.setting(selfBoost, true, true));

    private boolean hurt = false;
    private int boostTickCounter = 0;

    public CubeCraftFlight(String name, Module parent) {
        super(name, parent);
    }

    @EventHandler
    public final void onTick(final TickEvent tickEvent) {
        if (!Minecraft.inGame())
            return;

        if (!selfBoost.getValue())
            return;

        if (boostTickCounter < boostTicks.getValue()) {
            boostTickCounter++;
            return;
        }

        if (boostTickCounter >= boostTicks.getValue())
            boostTickCounter = 0;

        hurt = false;

        client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY(), client.player.getZ(), false));
        client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY() + 3.25, client.player.getZ(), false));
        client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY(), client.player.getZ(), false));
        client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(client.player.getX(), client.player.getY(), client.player.getZ(), true));

        boostTickCounter++;
    }

    @EventHandler
    public final void onMovement(final MovementEvent movementEvent) {
        if (client.player.hurtTime > 0 && !hurt) {
            hurt = true;
            MovementUtils.setSpeed(horizontalSpeed.getValue());
        }

        if (!hurt)
            return;

        if (client.player.input.jumping) {
            movementEvent.velocityY = verticalSpeed.getValue();
        } else if (client.player.input.sneaking) {
            movementEvent.velocityY = -verticalSpeed.getValue();
        } else {
            movementEvent.velocityY = 0;
        }

        if (constantSpeed.getValue()) {
            if (MovementUtils.isMoving())
                MovementUtils.setSpeed(horizontalSpeed.getValue());
        }
    }

    @EventHandler
    public final void onEnable() {
        hurt = false;
    }

}
