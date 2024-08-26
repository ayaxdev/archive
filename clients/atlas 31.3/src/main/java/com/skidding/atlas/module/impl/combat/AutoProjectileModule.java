package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.player.action.AttackEntityEvent;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.event.impl.player.rotation.RotationEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.impl.storage.TargetStorage;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.builder.impl.SliderBuilder;
import com.skidding.atlas.util.minecraft.player.PlayerUtil;
import com.skidding.atlas.util.minecraft.player.RotationUtil;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.Vec3;

public final class AutoProjectileModule extends ModuleFeature {

    public EntityLivingBase target;
    public TimerUtil timer = new TimerUtil(),
            switchTimer = new TimerUtil();
    private boolean canRod;
    private int oldSlot;

    public final SettingFeature<Float> range = new SliderBuilder("Range", 4, 4, 6, 1).build();

    public AutoProjectileModule() {
        super(new ModuleBuilder("AutoProjectile", "Automatically throws projectiles at targets", ModuleCategory.COMBAT));
    }

    @EventHandler
    public void onRotation(RotationEvent rotationEvent) {
        target = TargetStorage.target;
        int newSlot = getProjectileSlot();

        if (newSlot == -1 || target == null) {
            canRod = false;
            return;
        }

        if (PlayerUtil.getLookRangeToEntity(target) < range.getValue() || target.hurtTime >= 3) {
            canRod = false;
            return;
        }

        double delay = 350;

        if (getPlayer().getDistanceToEntity(target) <= 8) {
            delay = 300;
        } else if (getPlayer().getDistanceToEntity(target) <= 8) {
            delay = 250;
        } else if (getPlayer().getDistanceToEntity(target) <= 7) {
            delay = 200;
        } else if (getPlayer().getDistanceToEntity(target) <= 6) {
            delay = 150;
        } else if (getPlayer().getDistanceToEntity(target) <= 5) {
            delay = 100;
        }

        if (!timer.hasElapsed((long) delay)) {
            canRod = false;
            return;
        }

        double multiplier = getPlayer().getDistanceToEntity(target) / 1.25;

        double deltaX = (target.posX - target.lastTickPosX) * multiplier;
        double deltaZ = (target.posZ - target.lastTickPosZ) * multiplier;
        double targetPosX = target.posX + deltaX;
        double targetPosZ = target.posZ + deltaZ;

        double targetPosY = (target.posY + target.getEyeHeight()) - 0.4;

        float[] rots = RotationUtil.getRotation(new Vec3(targetPosX, targetPosY, targetPosZ));

        rotationEvent.rotationYaw = rots[0];
        rotationEvent.rotationPitch = rots[1];

        if (getPlayer().inventory.currentItem != getProjectileSlot())
            oldSlot = getPlayer().inventory.currentItem;
        getPlayer().inventory.currentItem = newSlot;

        canRod = true;
    }

    @EventHandler
    public void onPlayerPackets(WalkingPacketsEvent walkingPacketsEvent) {
        if (walkingPacketsEvent.eventType == Event.EventType.POST) {
            if (canRod && getPlayer().canEntityBeSeen(target)) {
                if (getPlayer().inventory.currentItem == getProjectileSlot()) {
                    mc.rightClickMouse();

                    switchTimer.reset();
                    timer.reset();
                }
            }

            if ((switchTimer.hasElapsed(!canRod ? 300 : 500)) && oldSlot != Integer.MIN_VALUE) {
                getPlayer().inventory.currentItem = oldSlot;
                oldSlot = Integer.MIN_VALUE;
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        if (oldSlot != Integer.MIN_VALUE) {
            getPlayer().inventory.currentItem = oldSlot;
            oldSlot = Integer.MIN_VALUE;
        }
    }

    private int getProjectileSlot() {
        int item = -1;
        int stackSize = 0;

        for (int i = 36; i < 45; ++i) {
            if (getPlayer().inventoryContainer.getSlot(i).getStack() != null &&
                    (getPlayer().inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemSnowball
                            || getPlayer().inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemEgg
                            || getPlayer().inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemFishingRod)
                    && getPlayer().inventoryContainer.getSlot(i).getStack().stackSize >= stackSize) {
                item = i - 36;
                stackSize = getPlayer().inventoryContainer.getSlot(i).getStack().stackSize;
            }
        }

        return item;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
