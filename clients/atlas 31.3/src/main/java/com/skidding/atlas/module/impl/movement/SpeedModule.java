package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.event.impl.player.movement.MovementEvent;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.ProcessorManager;
import com.skidding.atlas.processor.impl.tracker.PlayerTracker;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class SpeedModule extends ModuleFeature {

    public final SettingFeature<String> speedMode = mode("Mode", "Motion", new String[]{"Motion", "Strafe", "Verus", "Vulcan", "NCP", "Spartan", "AAC 3.3.12", "BlocksMC"}).build();

    public final SettingFeature<Float> motionSpeed = slider("Motion", 0.5f, 0.1f, 3, 2).addDependency(speedMode, "Motion").build();

    private final PlayerTracker playerTracker = ProcessorManager.getSingleton().getByClass(PlayerTracker.class);
    private final TimerUtil timer = new TimerUtil();
    private boolean spartanBoost = true;
    private double bmcSpeed = 0.28;
    private boolean bmcWasOnGround = false, bmcTakingVelocity = false;
    private int bmcTicks = 0;

    public SpeedModule() {
        super(new ModuleBuilder("Speed", "Enhances your movement speed for faster travel", ModuleCategory.MOVEMENT));
    }

    @EventHandler
    public final void onMove(MovementEvent movementEvent) {
        switch (speedMode.getValue()) {
            case "Spartan" -> {
                if (timer.hasElapsed(3000)) {
                    spartanBoost = !spartanBoost;
                    timer.reset();
                }

                if (spartanBoost) {
                    mc.timer.timerSpeed = 1.6f;
                } else {
                    mc.timer.timerSpeed = 1.0f;
                }
            }
            case "BlocksMC" -> {
                if (getPlayer().onGround) {
                    bmcWasOnGround = true;
                    if (MovementUtil.INSTANCE.isMoving()) {
                        bmcSpeed = 0.6 + (double)MovementUtil.INSTANCE.getSpeedAmplifier() * 0.065;
                        bmcTicks = 1;
                    }
                } else if (bmcWasOnGround) {
                    bmcSpeed *= 0.53;
                    bmcSpeed += 0.026;
                    bmcWasOnGround = false;
                } else {
                    bmcSpeed *= 0.91;
                    bmcSpeed += 0.026;
                }
                if (bmcTakingVelocity) {
                    bmcTicks = -7;
                }
                if (++bmcTicks == 0 && !getPlayer().onGround) {
                    bmcSpeed = 0.28 + (double)MovementUtil.INSTANCE.getSpeedAmplifier() * 0.065;
                }
                MovementUtil.INSTANCE.strafe(this.bmcSpeed);
            }
        }
    }

    @EventHandler
    public final void onMotion(WalkingPacketsEvent walkingPacketsEvent) {
        if (walkingPacketsEvent.eventType.equals(Event.EventType.PRE)) {
            switch (speedMode.getValue()) {
                case "Motion" -> {
                    getPlayer().motionX = getPlayer().motionZ = 0;

                    if (MovementUtil.INSTANCE.isMoving()) {
                        MovementUtil.INSTANCE.strafe(motionSpeed.getValue());
                    }

                    if (getPlayer().onGround && MovementUtil.INSTANCE.isMoving()) {
                        getPlayer().jump();
                    }
                }
                case "Strafe" -> {
                    mc.gameSettings.keyBindSprint.pressed = MovementUtil.INSTANCE.isMoving();
                    mc.gameSettings.keyBindJump.pressed = MovementUtil.INSTANCE.isMoving();

                    if (MovementUtil.INSTANCE.isMoving()) {
                        MovementUtil.INSTANCE.strafe();
                    }
                }
                case "Vulcan" -> {
                    if (MovementUtil.INSTANCE.isMoving()) {
                        if (getPlayer().onGround) {
                            getPlayer().jump();
                            MovementUtil.INSTANCE.setSpeed(0.485);
                        } else {
                            if(playerTracker.offGroundTicks < 3) {
                                MovementUtil.INSTANCE.strafe();
                            } else if(playerTracker.offGroundTicks == 5) {
                                getPlayer().motionY = -0.17;
                            }
                        }
                    }
                }
                case "NCP" -> {
                    if (MovementUtil.INSTANCE.isMoving()) {
                        if (getPlayer().onGround) {
                            getPlayer().jump();
                        }

                        MovementUtil.INSTANCE.strafe((float) Math.max(MovementUtil.INSTANCE.getBaseMoveSpeed() / 1.1, MovementUtil.INSTANCE.getSpeed()));
                    } else {
                        getPlayer().motionX = 0;
                        getPlayer().motionZ = 0;
                    }
                }
                case "Spartan", "BlocksMC" -> {
                    if (getPlayer().onGround && MovementUtil.INSTANCE.isMoving()) {
                        getPlayer().jump();
                    }
                }
                case "Verus" -> {
                    if (MovementUtil.INSTANCE.isMoving()) {
                        if (getPlayer().onGround) {
                            getPlayer().jump();
                        }

                        MovementUtil.INSTANCE.strafe((float) (mc.gameSettings.keyBindForward.isPressed() ? MovementUtil.INSTANCE.getBaseMoveSpeed() * 1.3 : MovementUtil.INSTANCE.getBaseMoveSpeed()));
                    } else {
                        getPlayer().motionX = 0;
                        getPlayer().motionZ = 0;
                    }
                }
                case "AAC 3.3.12" -> {
                    if (MovementUtil.INSTANCE.isMoving()) {
                        if (getPlayer().onGround) {
                            getPlayer().jump();
                        } else {
                            getPlayer().motionY -= 0.022;
                            getPlayer().jumpMovementFactor = 0.032F;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPacket(HandlePacketEvent handlePacketEvent) {
        if (handlePacketEvent.eventType == Event.EventType.INCOMING) {
            if (handlePacketEvent.packet instanceof S12PacketEntityVelocity s12 && s12.getEntityID() == getPlayer().getEntityId()) {
                bmcTakingVelocity = true;
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        bmcTakingVelocity = false;
        bmcWasOnGround = false;
        spartanBoost = false;
        bmcSpeed = 0.28;
        bmcTicks = 0;

        mc.gameSettings.keyBindJump.pressed = false;
        if (getPlayer() != null) {
            getPlayer().speedInAir = 0.02f;
            mc.timer.timerSpeed = 1f;
        }
    }
}
