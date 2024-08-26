package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.config.setting.impl.number.LongSetting;
import net.jezevcik.argon.event.impl.TickEntitiesEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.game.InventoryUtils;
import net.jezevcik.argon.utils.math.MSTimer;
import net.jezevcik.argon.utils.objects.SupplierFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryManager extends Module {

    public final LongSetting delay = new LongSetting("Delay", 100, 0, 1000, 10, this.config);

    public final BooleanSetting allowMultipleActionsPerTick = new BooleanSetting("MultipleActionsPerTick", false, this.config);

    public final BooleanSetting allowSwords = new BooleanSetting("AllowSwords", true, this.config);
    public final BooleanSetting allowArmor = new BooleanSetting("AllowArmor", true, this.config);
    public final BooleanSetting allowRangeWeapons = new BooleanSetting("AllowRangeWeapons", true, this.config);
    public final BooleanSetting allowTools = new BooleanSetting("AllowTools", true, this.config);
    public final BooleanSetting allowFood = new BooleanSetting("AllowFood", true, this.config);
    public final BooleanSetting allowBlocks = new BooleanSetting("AllowBlocks", true, this.config);
    public final BooleanSetting dropNonBridgeBlocks = new BooleanSetting("DropNonBridgeBlocks", true, this.config)
            .visibility(SupplierFactory.setting(allowBlocks, true, true));
    public final BooleanSetting allowMisc = new BooleanSetting("AllowMisc", false, this.config);

    public final BooleanSetting betterArmorOnly = new BooleanSetting("BetterArmorOnly", true, this.config)
            .visibility(SupplierFactory.setting(allowArmor, true, true));
    public final BooleanSetting betterSwordOnly = new BooleanSetting("BetterSwordOnly", true, this.config)
            .visibility(SupplierFactory.setting(allowSwords, true, true));
    public final BooleanSetting betterToolsOnly = new BooleanSetting("BetterToolsOnly", true, this.config)
            .visibility(SupplierFactory.setting(allowTools, true, true));

    public final BooleanSetting capBlockAmount = new BooleanSetting("CapBlockAmount", false, this.config);
    public final BooleanSetting preciseThrowing = new BooleanSetting("PreciseDrops", true, this.config);
    public final LongSetting maximumBlocks = new LongSetting("MaximumBlockAmount", 64 * 3, 0, 64 * 10, 32, this.config)
            .visibility(SupplierFactory.setting(capBlockAmount, true, true));
    public final BooleanSetting maximumOnlyForBridgeBlocks = new BooleanSetting("MaximumOnlyForBridgeBlocks", true, this.config)
            .visibility(SupplierFactory.setting(capBlockAmount, true, true)).visibility(SupplierFactory.setting(dropNonBridgeBlocks, true, false));

    public final MSTimer timer = new MSTimer();

    public InventoryManager() {
        super(ModuleParams.builder()
                .name("InventoryManager")
                .category(ModuleCategory.PLAYER)
                .build());
    }

    @EventHandler
    public final void onEntityTick(final TickEntitiesEvent tickEntitiesEvent) {
        if (!Minecraft.inGame() || !(client.currentScreen instanceof InventoryScreen))
            return;

        if (!timer.reached(delay.getValue()))
            return;

        timer.reset();

        for (int i = 0; i < client.player.currentScreenHandler.slots.size(); i++) {
            final Slot slot = client.player.currentScreenHandler.getSlot(i);

            if (!(slot.inventory instanceof PlayerInventory)) {
                continue;
            }

            if (!slot.hasStack())
                continue;

            final ItemStack stack = slot.getStack();

            if (stack.getItem() == null)
                continue;

            final Item item = stack.getItem();

            if (item instanceof SwordItem) {
                if (!allowSwords.getValue()) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                    if (!allowMultipleActionsPerTick.getValue())
                        return;
                }

                if (betterSwordOnly.getValue()) {
                    double maxValue = -1;

                    for (final ItemStack holdingSword : InventoryUtils.getInInventory(SwordItem.class)) {
                        if (holdingSword.getItem() == null)
                            continue;

                        if (holdingSword == stack)
                            continue;

                        if (!(holdingSword.getItem() instanceof SwordItem))
                            continue;

                        maxValue = Math.max(maxValue, InventoryUtils.getAttackDamage(holdingSword));
                    }

                    if (maxValue >= InventoryUtils.getAttackDamage(stack)) {
                        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                        if (!allowMultipleActionsPerTick.getValue())
                            return;
                    }
                }
            } else if (item instanceof ArmorItem armorItem) {
                if (!allowArmor.getValue()) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                    if (!allowMultipleActionsPerTick.getValue())
                        return;
                }

                if (betterArmorOnly.getValue()) {
                    double maxValue = -1;

                    for (final ItemStack wearingArmor : InventoryUtils.getInInventory(ArmorItem.class)) {
                        if (wearingArmor.getItem() == null)
                            continue;

                        if (wearingArmor == stack)
                            continue;

                        if (!(wearingArmor.getItem() instanceof ArmorItem wearingArmorItem))
                            continue;

                        if (wearingArmorItem.getType() != armorItem.getType())
                            continue;

                        maxValue = Math.max(maxValue, InventoryUtils.getEffectiveProtection(wearingArmor));
                    }

                    if (maxValue >= InventoryUtils.getEffectiveProtection(stack)) {
                        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                        if (!allowMultipleActionsPerTick.getValue())
                            return;
                    }
                }
            } else if ((item instanceof BowItem || item instanceof CrossbowItem || item instanceof ProjectileItem || item instanceof FishingRodItem)) {
                if(!allowRangeWeapons.getValue()) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                    if (!allowMultipleActionsPerTick.getValue())
                        return;
                }
            } else if (item instanceof ToolItem toolItem) {
                if (!allowTools.getValue()) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                    if (!allowMultipleActionsPerTick.getValue())
                        return;
                }

                if (betterToolsOnly.getValue()) {
                    float maxValue = -1;

                    Block block = Blocks.DIRT;

                    if (toolItem instanceof PickaxeItem) {
                        block = Blocks.STONE;
                    } else if (toolItem instanceof AxeItem) {
                        block = Blocks.OAK_LOG;
                    }

                    for (final ItemStack holdingTool : InventoryUtils.getInInventory(ToolItem.class)) {
                        if (holdingTool.getItem() == null)
                            continue;

                        if (holdingTool == stack)
                            continue;

                        if (!(holdingTool.getItem() instanceof ToolItem holdingToolItem))
                            continue;

                        if (holdingToolItem.getClass() != toolItem.getClass())
                            continue;

                        maxValue = Math.max(maxValue, holdingToolItem.getMiningSpeed(stack, block.getDefaultState()));
                    }

                    if (maxValue >= toolItem.getMiningSpeed(stack, block.getDefaultState())) {
                        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                        if (!allowMultipleActionsPerTick.getValue())
                            return;
                    }
                }
            } else if (item.getComponents().contains(DataComponentTypes.FOOD)) {
                if (!allowFood.getValue()) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                    if (!allowMultipleActionsPerTick.getValue())
                        return;
                }
            } else if (item instanceof BlockItem) {
                if (!allowBlocks.getValue()) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                    if (!allowMultipleActionsPerTick.getValue())
                        return;
                }
            } else if (!allowMisc.getValue()) {
                client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                if (!allowMultipleActionsPerTick.getValue())
                    return;
            }

            if (dropNonBridgeBlocks.getValue()) {
                if (dropBlocks(stack, item, slot, i) && !allowMultipleActionsPerTick.getValue())
                    return;
            }

            if (capBlockAmount.getValue()) {
                final long startAmount = InventoryUtils.getAmountOfBlocksInInventory(maximumOnlyForBridgeBlocks.getValue());

                if (startAmount > maximumBlocks.getValue()) {
                    if (capBlocks(stack, item, slot, i) && !allowMultipleActionsPerTick.getValue())
                        return;
                }
            }
        }
    }

    private boolean dropBlocks(ItemStack itemStack, Item item, Slot slot, int i) {
        if (!(item instanceof BlockItem blockItem))
            return false;

        for (Class<? extends Block> itKlass : InventoryUtils.NOT_IDEAL_BRIDGE_BLOCKS) {
            if (itKlass.isAssignableFrom(blockItem.getBlock().getClass())) {
                client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);

                return true;
            }
        }

        return false;
    }

    private boolean capBlocks(ItemStack itemStack, Item item, Slot slot, int i) {
        if (!(item instanceof BlockItem blockItem))
            return false;

        if (maximumOnlyForBridgeBlocks.getValue()) {
            for (Class<? extends Block> itKlass : InventoryUtils.NOT_IDEAL_BRIDGE_BLOCKS) {
                if (itKlass.isAssignableFrom(blockItem.getBlock().getClass()))
                    return false;
            }
        }

        final long currentAmount = InventoryUtils.getAmountOfBlocksInInventory(maximumOnlyForBridgeBlocks.getValue());

        if (currentAmount <= maximumBlocks.getValue())
            return false;

        final long difference = currentAmount - maximumBlocks.getValue();

        if (difference < 64) {
            if (preciseThrowing.getValue())
                client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 0, SlotActionType.THROW, client.player);
        } else {
            client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, client.player);
        }

        return true;
    }

}
