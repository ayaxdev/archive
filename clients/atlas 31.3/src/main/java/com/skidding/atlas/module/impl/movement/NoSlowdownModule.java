package com.skidding.atlas.module.impl.movement;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.event.impl.player.movement.SlowdownEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.player.MovementUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowdownModule extends ModuleFeature {

    public NoSlowdownModule() {
        super(new ModuleBuilder("NoSlowdown", "Remain unaffected by any speed-reducing effects", ModuleCategory.MOVEMENT));
    }

    public final SettingFeature<String> noSlowdownMode = mode("Mode", "Vanilla", new String[]{"Vanilla", "Switch", "Delay", "Old NCP", "Updated NCP"}).build();

    public final SettingFeature<Boolean> swords = check("Swords", true).build();
    public final SettingFeature<Float> swordForwardMultiplier = slider("Sword forward multiplier", 1, 0, 1, 2)
            .addDependency(swords).build();
    public final SettingFeature<Float> swordStrafeMultiplier = slider("Sword strafe multiplier", 1, 0, 1, 2)
            .addDependency(swords).build();

    public final SettingFeature<Boolean> bows = check("Bows", true).build();
    public final SettingFeature<Float> bowForwardMultiplier = slider("Bow forward multiplier", 1, 0, 1, 2)
            .addDependency(bows).build();
    public final SettingFeature<Float> bowStrafeMultiplier = slider("Bow strafe multiplier", 1, 0, 1, 2)
            .addDependency(bows).build();

    public final SettingFeature<Boolean> consumables = check("Consumables", true).build();
    public final SettingFeature<Float> consumableForwardMultiplier = slider("Consumable forward multiplier", 1, 0, 1, 2)
            .addDependency(consumables).build();
    public final SettingFeature<Float> consumableStrafeMultiplier = slider("Consumable strafe multiplier", 1, 0, 1, 2)
            .addDependency(consumables).build();

    private boolean blocking;

    @EventHandler
    public final void onSlowdown(SlowdownEvent slowdownEvent) {
        ItemStack currentItem = getPlayer().getCurrentEquippedItem();
        if (currentItem == null || !getPlayer().isUsingItem() || !MovementUtil.INSTANCE.isMoving()) {
            return;
        }

        // slowdownEvent.sprint = true; is a fix, so you can sprint while using an item.

        if (swords.getValue() && currentItem.getItem() instanceof ItemSword) {
            slowdownEvent.sprint = true;
            slowdownEvent.forward = swordForwardMultiplier.getValue();
            slowdownEvent.strafe = swordStrafeMultiplier.getValue();
        }

        if (bows.getValue() && currentItem.getItem() instanceof ItemBow) {
            slowdownEvent.sprint = true;
            slowdownEvent.forward = bowForwardMultiplier.getValue();
            slowdownEvent.strafe = bowStrafeMultiplier.getValue();
        }

        if (consumables.getValue() && currentItem.getItem() instanceof ItemFood) {
            slowdownEvent.sprint = true;
            slowdownEvent.forward = consumableForwardMultiplier.getValue();
            slowdownEvent.strafe = consumableStrafeMultiplier.getValue();
        }
    }

    @EventHandler
    public final void onPlayerPacket(WalkingPacketsEvent walkingPacketsEvent) {
        ItemStack currentItem = getPlayer().getCurrentEquippedItem();
        if (currentItem == null || !getPlayer().isUsingItem() || !MovementUtil.INSTANCE.isMoving()) {
            return;
        }

        if(walkingPacketsEvent.eventType == Event.EventType.PRE) {
            switch (noSlowdownMode.getValue()) {
                case "Switch" -> {
                    sendPacket(new C09PacketHeldItemChange((getPlayer().inventory.currentItem + 1) % 9));
                    sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
                }
                case "Delay" -> {
                    if (!getPlayer().isBlocking()) {
                        blocking = false;
                    }

                    if (getPlayer().isBlocking() && getPlayer().ticksExisted % 5 == 0 && blocking) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        blocking = false;
                    }

                    if (getPlayer().isBlocking() && getPlayer().ticksExisted % 5 == 1 && !blocking) {
                        sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getCurrentEquippedItem()));
                        blocking = true;
                    }
                }
                case "Old NCP" -> {
                    if (getPlayer().isBlocking()) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                }
                case "Updated NCP" -> {
                    if (getPlayer().isBlocking()) {
                        sendPacket(new C08PacketPlayerBlockPlacement(null));
                    } else {
                        sendPacket(new C09PacketHeldItemChange((getPlayer().inventory.currentItem + 1) % 9));
                        sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
                    }
                }
            }
        }

        if (walkingPacketsEvent.eventType == Event.EventType.POST) {
            switch (noSlowdownMode.getValue()) {
                case "Old NCP" -> {
                    if (getPlayer().isBlocking()) {
                        sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getCurrentEquippedItem()));
                    }
                }
            }
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
