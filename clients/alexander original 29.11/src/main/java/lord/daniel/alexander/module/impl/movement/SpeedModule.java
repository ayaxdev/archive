package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.event.impl.game.UpdateMotionEvent;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.util.player.MoveUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "Speed", enumModuleType = EnumModuleType.MOVEMENT)
public class SpeedModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Vanilla", new String[]{"Vanilla", "NCP", "NCPFaster", "NCPLowHop", "Vulcan", "Verus", "VerusLow", "KarhuTimer", "KarhuLowHop"});
    private final NumberValue<Float> speed = new NumberValue<>("Speed", this, 0.6f, 0f, 5f, 1).addVisibleCondition(() -> mode.is("Vanilla"));

    // Verus
    private boolean wasOnGround = false;

    // Karhu
    private boolean karhuFlagged = false, karhuJumped = false;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(mode.getValue());
    };

    @EventLink
    public final Listener<UpdateMotionEvent> updateMotionEventListener = updateMotionEvent -> {
        if (!isMoving())
            return;

        if (updateMotionEvent.getStage() == UpdateMotionEvent.Stage.MID) {
            switch (mode.getValue()) {
                case "Vanilla" -> {
                    MoveUtil.getMoveUtil().setSpeed(speed.getValue());

                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                }
                case "Verus" -> {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        MoveUtil.getMoveUtil().setSpeed(0.48);
                    } else {
                        MoveUtil.getMoveUtil().strafe();
                    }
                }
                case "KarhuLowHop" -> {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        mc.timer.timerSpeed = 1.009f;
                    } else if (mc.thePlayer.motionY > 0.2101) {
                        mc.thePlayer.motionY = mc.thePlayer.motionY * 0.90f;
                    }
                    break;
                }
                case "KarhuTimer" -> {
                    if(mc.thePlayer.onGround && karhuJumped) {
                        karhuJumped = false;
                        karhuFlagged = false;
                    }

                    if(!karhuFlagged) {
                        if (mc.thePlayer.onGround) {
                            mc.timer.timerSpeed = 1;
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY *= 0.55;
                        } else {
                            mc.timer.timerSpeed = (float) (1 + (Math.random() - 0.5) / 100);
                        }
                    } else {
                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            karhuJumped = true;
                        }
                        mc.timer.timerSpeed = 1;
                    }
                }
                case "VerusLow" -> {
                    if (MoveUtil.getMoveUtil().isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            this.wasOnGround = true;
                        } else if (wasOnGround) {
                            if (!mc.thePlayer.isCollidedHorizontally) {
                                mc.thePlayer.motionY = -0.0784000015258789;
                            }
                            wasOnGround = false;
                        }
                        MoveUtil.getMoveUtil().setSpeed(0.33);
                    } else {
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                    }
                }
                case "NCP" -> {
                    if (mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 2F;
                        mc.thePlayer.motionY = 0.42f;
                        MoveUtil.getMoveUtil().setSpeed(0.48 + MoveUtil.getMoveUtil().getSpeedBoost(4));
                    } else {
                        mc.timer.timerSpeed = 1;
                        MoveUtil.getMoveUtil().setSpeed(MoveUtil.getMoveUtil().getSpeed(mc.thePlayer) + MoveUtil.getMoveUtil().getSpeedBoost(0.375F));
                    }

                    if (PlayerHandler.onTicks == 5) {
                        mc.thePlayer.motionY -= 0.1;
                    }
                }
                case "NCPLowHop" -> {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() == 0) {
                                MoveUtil.getMoveUtil().setSpeed(0.58f);
                            } else if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() == 1) {
                                MoveUtil.getMoveUtil().setSpeed(0.67f);
                            }
                        } else {
                            MoveUtil.getMoveUtil().setSpeed(0.485f);
                        }

                    } else if (mc.thePlayer.motionY < 0.16 && mc.thePlayer.motionY > 0.0) {
                        mc.thePlayer.motionY = -0.1;
                    }
                    MoveUtil.getMoveUtil().strafe();
                }
                case "NCPFaster" -> {
                    if (mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 2F;
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY = 0.42f;
                        MoveUtil.getMoveUtil().setSpeed(0.48 + MoveUtil.getMoveUtil().getSpeedBoost(5));
                    } else {
                        mc.timer.timerSpeed = (float) (1.02 - Math.random() / 50);
                    }

                    MoveUtil.getMoveUtil().strafe();
                }
                case "Vulcan" -> {
                    if (PlayerHandler.offTicks == 0) {
                        mc.thePlayer.jump();
                        MoveUtil.getMoveUtil().setSpeed(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6F : 0.485F);
                    } else if (PlayerHandler.offTicks < 3) {
                        MoveUtil.getMoveUtil().strafe();
                    } else if (PlayerHandler.offTicks == 5) {
                        mc.thePlayer.motionY = -0.175;
                    } else if (PlayerHandler.offTicks == 10) {
                        MoveUtil.getMoveUtil().setSpeed((float) (MoveUtil.getMoveUtil().getSpeed(mc.thePlayer) * 0.8));
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if(packetEvent.getPacket() instanceof S08PacketPlayerPosLook && mode.is("Karhu")) {
            karhuFlagged = true;
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
    }
}
