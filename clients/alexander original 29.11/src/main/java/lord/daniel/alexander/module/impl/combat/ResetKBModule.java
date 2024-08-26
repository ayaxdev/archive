package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.AttackEvent;
import lord.daniel.alexander.event.impl.game.OnTickEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.event.impl.game.SwingEvent;
import lord.daniel.alexander.handler.plaxer.PlayerHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "ResetKB", aliases = {"MoreKB", "KeepKB", "KBReset"}, categories = {EnumModuleType.COMBAT, EnumModuleType.GHOST})
public class ResetKBModule extends AbstractModule {

    private final MultiSelectValue mode = new MultiSelectValue("Mode", this, new String[]{"LegitSimple", "LegitWTap", "Packet"}, new String[]{"LegitSimple", "LegitWTap", "Packet"});
    private final StringModeValue packetMode = new StringModeValue("PacketMode", this, "Normal", new String[] {"Packet", "DoublePacket", "SinglePacket"}).addVisibleCondition(mode, "Packet");
    private final BooleanValue onlyIfFacingAnEntity = new BooleanValue("OnlyIfFacingAnEntity", this, true);
    private final MultiSelectValue resetEvent = new MultiSelectValue("ResetEvents", this, new String[]{"OnAttack", "OnHurtTime"}, new String[]{"OnAttack", "OnHurtTime", "OnSwing"});
    private final MultiSelectValue resetOnHurtTimes = new MultiSelectValue("ResetOnHurtTime", this, new String[]{" 10 "}, new String[]{" 0 ", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 ", " 10 "}).addVisibleCondition(() -> resetEvent.is("OnHurtTime"));

    private KillAuraModule killAuraModule;
    private EntityLivingBase entity = null;
    private int ticks = 0;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(mode.getValue().contains("Packet") ? "Packet" : "Legit");
    };

    @EventLink
    public final Listener<OnTickEvent> onTickEventListener = onTickEvent -> {
        if(mode.is("LegitWTap")) {
            if (entity != null) {
                if (entity.hurtTime >= 0 && mc.thePlayer.hurtTime == 0) {
                    if (mc.thePlayer.getDistanceToEntity(entity) > 1.5 && mc.thePlayer.getDistanceToEntity(entity) < 2) {
                        if (ticks < 2) {
                            unsprint();
                        } else {
                            sprint();
                        }
                    }
                    if (mc.thePlayer.getDistanceToEntity(entity) > 2 && mc.thePlayer.getDistanceToEntity(entity) < 3) {
                        if (ticks < 3) {
                            unsprint();
                        } else {
                            sprint();
                        }
                    }
                    if (mc.thePlayer.getDistanceToEntity(entity) > 3) {
                        if (ticks < 4) {
                            unsprint();
                        } else {
                            sprint();
                        }
                    }
                } else {
                    sprint();
                }
            }
        }
        if(mode.is("Packet") || mode.is("LegitSimple")) {
            if (killAuraModule == null)
                killAuraModule = ModuleStorage.getModuleStorage().getByClass(KillAuraModule.class);

            if(mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                return;
            }

            if (mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
                entity = (EntityLivingBase) mc.objectMouseOver.entityHit;
            }

            if (killAuraModule.isEnabled() && killAuraModule.target != null) {
                entity = killAuraModule.target;
            }

            if(entity == null)
                return;

            if(resetEvent.is("OnHurtTime") && this.resetOnHurtTimes.getValue().contains(" " + entity.hurtTime + " ")) {
                onReset();
            }
        }
    };

    private void sprint() {
        if (mc.currentScreen == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
        }
    }

    private void unsprint() {
        ticks++;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    }


    @EventLink
    public final Listener<AttackEvent> onAttackEventListener = attackEvent -> {
        if(mode.is("LegitWTap")) {
            if(attackEvent.getAttacking() instanceof EntityLivingBase) {
                ticks = 0;
                entity = (EntityLivingBase) attackEvent.getAttacking();
            }
        }
        if(mode.is("Packet") || mode.is("LegitSimple")) {
            if(mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                return;
            }

            if(resetEvent.is("OnAttack")) {
                onReset();
            }
        }
    };

    @EventLink
    public final Listener<SwingEvent> swingEventListener = swingEvent -> {
        if(mode.is("Packet") || mode.is("LegitSimple")) {
            if(mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                return;
            }

            if(resetEvent.is("OnSwing") && swingEvent.getEntity() == mc.thePlayer) {
                onReset();
            }
        }
    };

    public void onReset() {
        if(this.onlyIfFacingAnEntity.getValue() && entity == null)
            return;

        final double x = mc.thePlayer.posX - entity.posX;
        final double z = mc.thePlayer.posZ - entity.posZ;
        final float yaw = (float) (MathHelper.func_181159_b(z, x) * 180.0 / Math.PI - 90.0);
        final float yawDiff = Math.abs(MathHelper.wrapAngleTo180_float(yaw - entity.rotationYawHead));

        if (yawDiff > 120.0f) {
            return;
        }

        if(mode.is("Packet")) {
            switch (packetMode.getValueAsString()) {
                case "Packet": {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.setServerSprintState(true);
                    break;
                }
                case "DoublePacket": {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.setServerSprintState(true);
                    break;
                }
                case "SinglePacket": {
                    if (mc.thePlayer.isSprinting()) {
                        mc.thePlayer.setSprinting(false);
                    }
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    mc.thePlayer.setServerSprintState(true);
                    break;
                }
            }
        }

        if(mode.is("LegitSimple")) {
            PlayerHandler.shouldSprintReset = true;
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
