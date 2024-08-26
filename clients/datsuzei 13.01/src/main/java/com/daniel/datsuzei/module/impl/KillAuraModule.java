package com.daniel.datsuzei.module.impl;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.event.impl.PositionPacketEvent;
import com.daniel.datsuzei.event.impl.RotationEvent;
import com.daniel.datsuzei.event.impl.ToClickEvent;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.settings.impl.BooleanSetting;
import com.daniel.datsuzei.settings.impl.NumberSetting;
import com.daniel.datsuzei.util.math.MSTimer;
import com.daniel.datsuzei.util.player.PlayerUtil;
import com.daniel.datsuzei.util.player.RotationUtil;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjglx.input.Keyboard;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class KillAuraModule extends ModuleFeature {

    public final NumberSetting<Float> range = new NumberSetting<>("Range", 3.0f, 0.0f, 6.0f);
    public final BooleanSetting switchTargets = new BooleanSetting("SwitchTargets", false);
    public final BooleanSetting resetRotation = new BooleanSetting("ResetRotation", false);
    public final BooleanSetting keepRotations = new BooleanSetting("KeepRotations", true);
    public final NumberSetting<Integer> maximumKeepRotationLength = new NumberSetting<>("KeepLength", -1, -1, 20);

    public final BooleanSetting players = new BooleanSetting("Players", true);
    public final BooleanSetting monsters = new BooleanSetting("Monsters", false);
    public final BooleanSetting animals = new BooleanSetting("Animals", false);
    public final BooleanSetting invisible = new BooleanSetting("Invisible", false);

    private final MSTimer attackTimer = new MSTimer();

    private EntityLivingBase target;
    private float lastSetYaw, lastSetPitch;
    private boolean needsReset;

    public KillAuraModule() {
        super(new ModuleData("KillAura", "Automatically attacks entities", ModuleCategory.COMBAT),
                new BindableData(Keyboard.KEY_R), null);
    }

    @Listen
    public final Listener<PositionPacketEvent> positionPacketEventListener = _ -> {
        DatsuzeiClient.getSingleton().getThreadpool().submit(() -> {
            final ArrayList<Entity> possibleTargets = new ArrayList<>(mc.theWorld.loadedEntityList.stream().filter(entity -> {
                if(!validEntityType(entity))
                    return false;

                // Do faster, Euclidean distance calculation between positions to eliminate 99% of non-targetable entities
                if(mc.thePlayer.getDistanceToEntity(entity) > (range.getValue() + 2.0F))
                    return false;

                return !(PlayerUtil.getLookRangeToEntity(entity) > range.getValue());
            }).toList());

            possibleTargets.sort((o1, o2) -> (int) (mc.thePlayer.getDistanceToEntity(o1) - mc.thePlayer.getDistanceToEntity(o2)));

            if(!possibleTargets.isEmpty()) {
                final EntityLivingBase nextTarget = (EntityLivingBase) possibleTargets.get(0);

                if(target != null) {
                    if(switchTargets.getValue())
                        target = nextTarget;
                } else {
                    target = nextTarget;
                }
            }
        });
    };

    @Listen
    public final Listener<ToClickEvent> toClickEventListener = _ -> {
        if(target != null) {
            if(mc.objectMouseOver != null && validEntityType(mc.objectMouseOver.entityHit) &&
                    allowHit((EntityLivingBase) mc.objectMouseOver.entityHit)) {
                mc.clickMouse();
                attackTimer.reset();
            }
        }
    };

    private boolean validEntityType(Entity e) {
        if(!(e instanceof EntityLivingBase))
            return false;

        if(e instanceof EntityPlayer && e != mc.thePlayer && players.getValue())
            return true;

        if(e instanceof EntityMob && monsters.getValue())
            return true;

        return e instanceof EntityAnimal && animals.getValue();
    }

    private boolean allowHit(EntityLivingBase player) {
        if(!attackTimer.hasReached(100))
            return false;

        return player.hurtTime <= 1 || (mc.thePlayer.hurtTime > 0 && mc.thePlayer.hurtTime <= 10);
    }

    @Listen
    public final Listener<RotationEvent> rotationEventListener = rotationEvent -> {
        if(target != null) {
            if(!mc.theWorld.loadedEntityList.contains(target) || target.isDead || PlayerUtil.getLookRangeToEntity(target) > range.getValue()) {
                target = null;
                return;
            }

            try {
                if(mc.objectMouseOver != null && mc.objectMouseOver.entityHit == target && keepRotations.getValue() && (maximumKeepRotationLength.getValue() == -1 || mc.thePlayer.ticksExisted % maximumKeepRotationLength.getValue() != 0)) {
                    rotationEvent.yaw = lastSetYaw = RotationUtil.rotationYaw;
                    rotationEvent.pitch = lastSetPitch =  RotationUtil.rotationPitch;
                } else {
                    Vec3 aimVector = RotationUtil.getBestLookVector(mc.thePlayer.getPositionEyes(1F), target.getEntityBoundingBox());
                    aimVector = aimVector.addVector(SecureRandom.getInstanceStrong().nextDouble() / 50, SecureRandom.getInstanceStrong().nextDouble() / 50, SecureRandom.getInstanceStrong().nextDouble() / 50);

                    float[] rotation = RotationUtil.getRotation(aimVector);

                    rotation[0] = RotationUtil.clampDelta(RotationUtil.rotationYaw, rotation[0], 10);
                    rotation[1] = RotationUtil.clampDelta(RotationUtil.rotationPitch, rotation[1], 10);

                    rotation = RotationUtil.applyMouseSensitivity(rotation);

                    rotationEvent.yaw = lastSetYaw = rotation[0];
                    rotationEvent.pitch = lastSetPitch = rotation[1];
                }

                needsReset = true;
            } catch (NoSuchAlgorithmException e) {
                DatsuzeiClient.getSingleton().getLogger().error("getInstanceStrong() does not exist???", e);
            }
        } else if(needsReset) {
            if(resetRotation.getValue()) {
                mc.thePlayer.rotationYaw = lastSetYaw;
                mc.thePlayer.rotationPitch = lastSetPitch;
            } else {
                mc.thePlayer.rotationYaw = RotationUtil.adjustYaw(lastSetYaw, mc.thePlayer.rotationYaw);
            }
            needsReset = false;
        }
    };

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {
        if(needsReset) {
            if(resetRotation.getValue()) {
                mc.thePlayer.rotationYaw = lastSetYaw;
                mc.thePlayer.rotationPitch = lastSetPitch;
            } else {
                mc.thePlayer.rotationYaw = RotationUtil.adjustYaw(lastSetYaw, mc.thePlayer.rotationYaw);
            }
            needsReset = false;
        }
    }

}
