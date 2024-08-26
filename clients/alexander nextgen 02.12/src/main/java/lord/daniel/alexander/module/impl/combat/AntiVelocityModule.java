package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.radbus.Listen;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.event.impl.player.*;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.math.time.MSTimer;
import lord.daniel.alexander.util.player.MoveUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@CreateModule(name = "Velocity", displayNames = {"AntiVelocity", "AntiKnockback", "KnockbackModifier", "VelocityModifier", "AntiKB", "AntiVelo", "Velo", "VeloModifier", "KBModifier"}, category = EnumModuleType.COMBAT)
public class AntiVelocityModule extends AbstractModule {

    private final StringModeValue mode = new StringModeValue("Mode", this, "CustomSimple", new String[]{"CustomSimple", "Stack", "FollowFirstTick", "TickCancel", "BackToBlock", "Vulcan2.0.1", "Vulcan2.7.7", "Intave13KeepLow", "Intave13Reverse", "Intave13Wall", "Intave14.1.2", "Intave14.1.2Stronger", "AAC3.3.12", "AAC3.3.14", "AAC4", "MatrixGroundReduce", "GrimSpoof", "InvalidPacket", "Collision", "LegitReduce", "StrafeFreeze", "Jump", "Ground", "Reverse"});

    // CustomSimple
    private final BooleanValue cancelHorizontal = new BooleanValue("CancelHorizontal", this, true).addVisibleCondition(() -> mode.is("CustomSimple"));
    private final BooleanValue negativeHorizontal = new BooleanValue("NegativeHorizontal", this, false).addVisibleCondition(() -> mode.is("CustomSimple") && !cancelHorizontal.getValue());
    private final NumberValue<Integer> horizontalPercentage = new NumberValue<>("HorizontalPercentage", this, 0, 0, 100).addVisibleCondition(() -> mode.is("CustomSimple") && !cancelHorizontal.getValue() && !negativeHorizontal.getValue());
    private final NumberValue<Integer> negativeHorizontalPercentage = new NumberValue<>("NegativeHorizontalPercentage", this, 0, 0, 100).addVisibleCondition(() -> mode.is("CustomSimple") && !cancelHorizontal.getValue() && negativeHorizontal.getValue());
    private final BooleanValue cancelVertical = new BooleanValue("CancelVertical", this, true).addVisibleCondition(() -> mode.is("CustomSimple"));
    private final BooleanValue negativeVertical = new BooleanValue("NegativeVertical", this, false).addVisibleCondition(() -> mode.is("CustomSimple") && !cancelVertical.getValue());
    private final NumberValue<Integer> verticalPercentage = new NumberValue<>("VerticalPercentage", this, 0, 0, 100).addVisibleCondition(() -> mode.is("CustomSimple") && !cancelVertical.getValue() && !negativeVertical.getValue());
    private final NumberValue<Integer> negativeVerticalPercentage = new NumberValue<>("NegativeVerticalPercentage", this, 0, 0, 100).addVisibleCondition(() -> mode.is("CustomSimple") && !cancelVertical.getValue() && negativeVertical.getValue());

    // TickCancel
    private final NumberValue<Integer> tickCancel = new NumberValue<>("CancelTick", this, 3, 1, 20).addVisibleCondition(() -> mode.is("TickCancel"));

    // Reverse
    private final NumberValue<Integer> reversePercentage = new NumberValue<>("ReversePercentage", this, 100, 0, 100).addVisibleCondition(() -> mode.is("Reverse"));

    // BackToBlock
    private final NumberValue<Integer> backToBlockHurtTime = new NumberValue<>("HurtTime", this, 5, 1, 10).addVisibleCondition(() -> mode.is("BackToBlock"));

    // LegitReduce
    private final NumberValue<Float> multiplier = new NumberValue<>("Reduce", this, 0.6f, -1f, 1f).addVisibleCondition(() -> mode.is("LegitReduce"));

    // Stack
    private int row = 0;

    // FollowFirstTick
    private double motionY, motionX, motionZ;
    private boolean lastTick;

    // TickCancel
    private boolean countingTicks = false;
    private int cancelTicks = 0;

    // Intave13KeepLow
    private boolean wasOnGround = false;

    // GrimSpoof
    private final Queue<Short> transactionQueue = new ConcurrentLinkedQueue<Short>();
    private boolean grimPacket;

    // MatrixGroundReduce
    private boolean matrixGround = false;

    // Multiple Modes
    private boolean velocityInput = false;
    private double velocityX, velocityY, velocityZ;
    private final MSTimer velocityTimer = new MSTimer();


    @Listen
    public final void onVelocity(VelocityEvent velocityEvent) {
        final MoveUtil moveUtil = MoveUtil.getMoveUtil();
        
        if (velocityEvent.getEntity() == mc.thePlayer) {
            velocityInput = true;
            velocityX = velocityEvent.getMotionX();
            velocityY = velocityEvent.getMotionY();
            velocityZ = velocityEvent.getMotionZ();
            velocityTimer.reset();

            switch (mode.getValue()) {
                case "AAC5.2.0", "Vulcan2.7.7" -> {
                    velocityEvent.setCancelled(true);
                }
                case "CustomSimple" -> {
                    if (!cancelHorizontal.getValue()) {
                        if (negativeHorizontal.getValue()) {
                            velocityEvent.setMotionX(velocityEvent.getMotionX() * -(negativeHorizontalPercentage.getValue() / 100D));
                            velocityEvent.setMotionZ(velocityEvent.getMotionZ() * -(negativeHorizontalPercentage.getValue() / 100D));
                        } else {
                            velocityEvent.setMotionX(velocityEvent.getMotionX() * (horizontalPercentage.getValue() / 100D));
                            velocityEvent.setMotionZ(velocityEvent.getMotionZ() * (horizontalPercentage.getValue() / 100D));
                        }
                    } else {
                        velocityEvent.setIgnoreX(true);
                        velocityEvent.setIgnoreZ(true);
                    }

                    if (!cancelVertical.getValue()) {
                        if (negativeVertical.getValue()) {
                            velocityEvent.setMotionY(velocityEvent.getMotionY() * (-negativeVerticalPercentage.getValue() / 100D));
                        } else {
                            velocityEvent.setMotionY(velocityEvent.getMotionY() * (verticalPercentage.getValue() / 100D));
                        }
                    } else {
                        velocityEvent.setIgnoreY(true);
                    }
                }

                case "MatrixGroundReduce" -> {
                    velocityEvent.setMotionX(velocityEvent.getMotionX() * 0.36);
                    velocityEvent.setMotionZ((velocityEvent.getMotionZ() * 0.36));
                    if (matrixGround) {
                        velocityEvent.setMotionX(velocityEvent.getMotionX() * 0.6);
                        velocityEvent.setMotionZ(velocityEvent.getMotionZ() * 0.6);
                        mc.thePlayer.onGround = false;
                    }
                }

                case "Stack" -> {
                    row++;
                    if (moveUtil.isMoving()) {
                        if (row <= 2) {
                            velocityEvent.setCancelled(true);
                        } else {
                            row = 0;
                        }
                    } else {
                        velocityEvent.setCancelled(true);
                    }
                }

                case "FollowFirstTick" -> {
                    velocityEvent.setCancelled(true);
                    motionY = velocityEvent.getMotionY();
                    motionX = velocityEvent.getMotionX();
                    motionZ = velocityEvent.getMotionZ();
                    mc.thePlayer.motionY = 0.03;
                    lastTick = true;
                }

                case "TickCancel" -> {
                    countingTicks = true;
                    cancelTicks = 0;
                }

                case "Reverse" -> {
                    velocityEvent.setMotionX(velocityEvent.getMotionX() * -(reversePercentage.getValue() / 100d));
                    velocityEvent.setMotionZ(velocityEvent.getMotionZ() * -(reversePercentage.getValue() / 100d));
                }

                case "InvalidPacket" -> {
                    sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                    velocityEvent.setCancelled(true);
                }
            }
        }
    };

    @Listen
    public final void onLivingUpdate(LivingUpdateEvent livingUpdateEvent) {
        if (velocityTimer.hasReached(1000)) {
            velocityInput = false;
        }

        switch (mode.getValue()) {
            case "MatrixGroundReduce" -> {
                matrixGround = mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown();
            }
        }
    };

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        final MoveUtil moveUtil = MoveUtil.getMoveUtil();

        if (updateEvent.getStage() == UpdateEvent.Stage.MID) {
            if (countingTicks) {
                cancelTicks++;
            }
        }

        switch (mode.getValue()) {
            case "Ground" -> {
                if (velocityInput) {
                    mc.thePlayer.onGround = true;
                    velocityInput = false;
                }
            }

            case "FollowFirstTick" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID && lastTick) {
                    mc.thePlayer.posX += motionX;
                    mc.thePlayer.posY += motionY;
                    mc.thePlayer.posZ += motionZ;
                    lastTick = false;
                }
            }

            case "BackToBlock" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID && getPlayer().hurtTime == backToBlockHurtTime.getValue()) {
                    getPlayer().motionX *= -1;
                    getPlayer().motionZ *= -1;
                    if (getPlayer().motionY < 0) {
                        getPlayer().motionY *= -1;
                    }
                }
            }

            case "StrafeFreeze" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID && getPlayer().hurtTime == 8) {
                    MoveUtil.getMoveUtil().setSpeed(0.025);
                }
            }

            case "Jump" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID && getHurtTime() == 10 && getPlayer().onGround) {
                    getPlayer().jump();
                }
            }

            case "Vulcan2.0.1" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID && getPlayer().hurtTime != 0) {
                    MoveUtil.getMoveUtil().setSpeed(0.2);
                }
            }

            case "Intave13KeepLow" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID) {
                    switch (getHurtTime()) {
                        case 10:
                            if (getPlayer().onGround) {
                                wasOnGround = true;
                            }
                            break;
                        case 0:
                            wasOnGround = false;
                            break;
                        case 9:
                            if (wasOnGround) {
                                getPlayer().motionY = 0.0D;
                            }
                            break;
                    }
                }
            }

            case "Intave13Wall" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID) {
                    if (mc.inGameHasFocus) {
                        final float velocity = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.3045, 0.3345);
                        if (getPlayer().isCollidedHorizontally && !getPlayer().onGround && !getPlayer().isCollidedVertically && !getPlayer().isInWeb && !getPlayer().isInWater() && !getPlayer().isInLava() && getHurtTime() != 0) {
                            MoveUtil.getMoveUtil().setSpeed(velocity, getRotationYaw());
                        }
                    }
                }
            }

            case "AAC3.3.12" -> {
                if (getPlayer().hurtTime > 0 && updateEvent.getStage() == UpdateEvent.Stage.MID) {
                    getPlayer().motionX *= 0.8;
                    getPlayer().motionZ *= 0.8;
                    getPlayer().motionY *= 1;
                }
            }

            case "AAC3.3.14" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID && getPlayer().hurtTime > 0) {
                    mc.thePlayer.setVelocity(0, 0, 0);
                }
            }

            case "AAC4" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID) {
                    if (getHurtTime() > 5) {
                        getPlayer().onGround = true;
                        moveUtil.stopWalk();
                    } else if (getHurtTime() != 0) {
                        moveUtil.resumeWalk();
                    }
                }
            }

            case "GrimSpoof" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID) {
                    if (!this.transactionQueue.isEmpty() || !this.grimPacket) {
                        break;
                    }
                    this.grimPacket = false;
                }
            }

            case "Polar" -> {
                if (updateEvent.getStage() == UpdateEvent.Stage.MID
                        && mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)
                        && mc.thePlayer.hurtTime > 0
                        && !mc.thePlayer.isSwingInProgress) {
                    mc.thePlayer.motionX *= 0.45D;
                    mc.thePlayer.motionZ *= 0.45D;
                    mc.thePlayer.setSprinting(false);
                }
            }
        }
    };


    @Listen
    public final void onMoveFlying(MoveFlyingEvent moveFlyingEvent) {
        switch (mode.getValue()) {
            case "TickCancel" -> {
                if (cancelTicks >= tickCancel.getValue() && countingTicks) {
                    mc.thePlayer.setVelocity(0, 0, 0);
                    countingTicks = false;
                }
            }

            case "Intave13Reverse" -> {
                if (getPlayer() != null) {
                    if (getHurtTime() > 0) {
                        getPlayer().setSprinting(false);
                        MoveUtil.getMoveUtil().setSpeed(0.05F);
                    }
                }
            }
        }
    }

    @Listen
    public final void onKnockbackModifier(KnockbackModifierEvent knockbackModifierEvent) {
        switch (mode.getValue()) {
            case "Intave14.1.2" -> {
                knockbackModifierEvent.setFlag(true);
            }

            case "Intave14.1.2Stronger" -> {
                knockbackModifierEvent.setFlag(true);
                int i = 0;
                i += EnchantmentHelper.getKnockbackModifier(getPlayer());
                if (getPlayer().isSprinting()) {
                    i++;
                }
                if (getPlayer().isSwingInProgress && getPlayer().hurtTime != 0 && getPlayer().moveForward != 0 && getPlayer().moveStrafing != 0) {
                    if (getPlayer().onGround || !getPlayer().isSprinting()) {
                        if (i > 0) {
                            getPlayer().addVelocity((-MathHelper.sin((float) (getRotationYaw() * Math.PI / 180)) * i * 0.5), 0.1, (MathHelper.cos((float) (getRotationYaw() * Math.PI / 180)) * i * 0.5));
                        }
                    }
                }
            }

            case "LegitReduce" -> {
                if(!knockbackModifierEvent.isFlag()) {
                    knockbackModifierEvent.getEntity().motionX *= multiplier.getValue();
                    knockbackModifierEvent.getEntity().motionZ *= multiplier.getValue();
                    knockbackModifierEvent.getEntity().setSprinting(false);
                }
            }
        }
    };

    @Listen
    public final void onBlockAABB(BlockAABBEvent blockAABBEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        switch (mode.getValue()) {
            case "Collision" -> {
                if (blockAABBEvent.getBlock() instanceof BlockAir && mc.thePlayer.hurtTime > 0 && mc.thePlayer.ticksSinceVelocity <= 9) {
                    final double x = blockAABBEvent.getBlockPos().getX(), y = blockAABBEvent.getBlockPos().getY(), z = blockAABBEvent.getBlockPos().getZ();

                    if (y == Math.floor(mc.thePlayer.posY) + 1) {
                        blockAABBEvent.setBoundingBox(AxisAlignedBB.fromBounds(0, 0, 0, 1, 0, 1).offset(x, y, z));
                    }
                }
            }
        }
    };

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        switch (mode.getValue()) {
            case "GrimSpoof" -> {
                if (packetEvent.getStage() == PacketEvent.Stage.RECEIVING) {
                    Packet<?> p = packetEvent.getPacket();
                    if (p instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) p).getEntityID() == mc.thePlayer.getEntityId()) {
                        packetEvent.setCancelled(true);
                        grimPacket = true;
                    } else if (p instanceof S32PacketConfirmTransaction) {
                        if (!grimPacket) {
                            return;
                        }
                        packetEvent.setCancelled(true);
                        transactionQueue.add(((S32PacketConfirmTransaction) p).getActionNumber());
                    }
                } else {
                    if (packetEvent.getPacket() instanceof C0FPacketConfirmTransaction) {
                        if (!grimPacket || transactionQueue.isEmpty()) {
                            return;
                        }
                        if (transactionQueue.remove(((C0FPacketConfirmTransaction) packetEvent.getPacket()).getUid())) {
                            packetEvent.setCancelled(true);
                        }
                    }
                }
            }

            case "Vulcan2.7.7" -> {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (mc.thePlayer.hurtTime > 0 && packetEvent.getPacket() instanceof C0FPacketConfirmTransaction) {
                        packetEvent.setCancelled(true);
                    }
                }
            }
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

}
