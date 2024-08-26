package net.jezevcik.argon.module.impl;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.config.setting.impl.BooleanSetting;
import net.jezevcik.argon.config.setting.impl.number.LongSetting;
import net.jezevcik.argon.event.impl.TickEntitiesEvent;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.params.ModuleCategory;
import net.jezevcik.argon.module.params.ModuleParams;
import net.jezevcik.argon.utils.game.InventoryUtils;
import net.jezevcik.argon.utils.math.MSTimer;
import net.jezevcik.argon.utils.objects.SupplierFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class ChestStealerModule extends Module {

    public final LongSetting delay = new LongSetting("Delay", 100, 0, 1000, 10, this.config);

    public final BooleanSetting allowSwords = new BooleanSetting("AllowSwords", true, this.config);
    public final BooleanSetting allowArmor = new BooleanSetting("AllowArmor", true, this.config);
    public final BooleanSetting allowRangedWeapons = new BooleanSetting("AllowRangedWeapons", true, this.config);
    public final BooleanSetting allowTools = new BooleanSetting("AllowTools", true, this.config);
    public final BooleanSetting allowFood = new BooleanSetting("AllowFood", true, this.config);
    public final BooleanSetting allowBlocks = new BooleanSetting("AllowBlocks", true, this.config);
    public final BooleanSetting allowMisc = new BooleanSetting("AllowMisc", false, this.config);

    public final BooleanSetting maxBlocks = new BooleanSetting("CapBlockAmount", false, this.config)
            .visibility(SupplierFactory.setting(allowBlocks, true, this));
    public final LongSetting maximumBlocks = new LongSetting("MaximumBlockAmount", 64 * 3, 0, 64 * 10, 32, this.config)
            .visibility(SupplierFactory.setting(maxBlocks, true, true));
    public final BooleanSetting maximumOnlyForBridgeBlocks = new BooleanSetting("MaximumOnlyForBridgeBlocks", true, this.config)
            .visibility(SupplierFactory.setting(maxBlocks, true, true));

    public final BooleanSetting betterArmorOnly = new BooleanSetting("BetterArmorOnly", true, this.config)
            .visibility(SupplierFactory.setting(allowArmor, true, this));
    public final BooleanSetting betterSwordOnly = new BooleanSetting("BetterSwordOnly", true, this.config)
            .visibility(SupplierFactory.setting(allowSwords, true, this));
    public final BooleanSetting betterToolsOnly = new BooleanSetting("BetterToolsOnly", true, this.config)
            .visibility(SupplierFactory.setting(allowTools, true, this));

    public final BooleanSetting close = new BooleanSetting("Close", true, this.config);

    private final MSTimer delayTimer = new MSTimer();

    public ChestStealerModule() {
        super(ModuleParams.builder()
                .name("ChestStealer")
                .category(ModuleCategory.WORLD)
                .build());
    }

    @EventHandler
    public final void onTickEntities(TickEntitiesEvent tickEntitiesEvent) {
        if (client.player.currentScreenHandler instanceof GenericContainerScreenHandler chest) {
            int selected = -1;

            for (int i = 0; i < chest.getInventory().size(); i++) {
                final Slot slot = chest.getSlot(i);

                if (!slot.hasStack())
                    continue;

                final ItemStack stack = slot.getStack();

                if (!isValid(stack))
                    continue;

                selected = i;

                break;
            }

            if (selected != -1) {
                if (delayTimer.reached(delay.getValue())) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, selected, 0, SlotActionType.QUICK_MOVE, client.player);

                    delayTimer.reset();
                }
            } else {
                if (close.getValue())
                    client.player.closeHandledScreen();
            }

        }
    }

    // TODO: this
    private boolean isValid(ItemStack itemStack) {
        if (itemStack.getItem() == null)
            return false;

        final Item item = itemStack.getItem();

        switch (item) {
            case BlockItem blockItem -> {
                if (!allowBlocks.getValue())
                    return false;

                if (maxBlocks.getValue()) {
                    if (maximumOnlyForBridgeBlocks.getValue()) {
                        final Class<? extends Block> klass = blockItem.getBlock().getClass();

                        for (final Class<? extends Block> itKlass : InventoryUtils.NOT_IDEAL_BRIDGE_BLOCKS) {
                            if (itKlass.isAssignableFrom(klass))
                                return true;
                        }
                    }

                    final long amount = InventoryUtils.getAmountOfBlocksInInventory(true);
                    return amount < maximumBlocks.getValue();
                }
            }
            case ArmorItem armorItem -> {
                if (!allowArmor.getValue())
                    return false;

                if (betterArmorOnly.getValue()) {
                    double maxValue = -1;

                    for (final ItemStack wearingArmor : InventoryUtils.getInInventory(ArmorItem.class)) {
                        if (wearingArmor.getItem() == null)
                            continue;

                        if (!(wearingArmor.getItem() instanceof ArmorItem wearingArmorItem))
                            continue;

                        if (wearingArmorItem.getType() != armorItem.getType())
                            continue;

                        maxValue = Math.max(maxValue, InventoryUtils.getEffectiveProtection(wearingArmor));
                    }

                    return maxValue < InventoryUtils.getEffectiveProtection(itemStack);
                }
            }
            case SwordItem ignored -> {
                if (!allowSwords.getValue())
                    return false;

                if (betterSwordOnly.getValue()) {
                    double maxValue = -1;

                    for (final ItemStack holdingSword : InventoryUtils.getInInventory(SwordItem.class)) {
                        if (holdingSword.getItem() == null)
                            continue;

                        if (!(holdingSword.getItem() instanceof SwordItem))
                            continue;

                        maxValue = Math.max(maxValue, InventoryUtils.getAttackDamage(holdingSword));
                    }

                    return maxValue < InventoryUtils.getAttackDamage(itemStack);
                }
            }
            case ToolItem toolItem -> {
                if (!allowSwords.getValue())
                    return false;

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

                        if (!(holdingTool.getItem() instanceof ToolItem holdingToolItem))
                            continue;

                        if (holdingToolItem.getClass() != toolItem.getClass())
                            continue;

                        maxValue = Math.max(maxValue, holdingToolItem.getMiningSpeed(itemStack, block.getDefaultState()));
                    }

                    return maxValue < toolItem.getMiningSpeed(itemStack, block.getDefaultState());
                }
            }

            case BowItem ignored -> {
                if (!allowRangedWeapons.getValue())
                    return false;
            }

            case ProjectileItem ignored -> {
                if (!allowRangedWeapons.getValue())
                    return false;
            }

            case FishingRodItem ignored -> {
                if (!allowRangedWeapons.getValue())
                    return false;
            }

            case CrossbowItem ignored -> {
                if (!allowRangedWeapons.getValue())
                    return false;
            }

            default -> {
                if (itemStack.getItem().getComponents().contains(DataComponentTypes.FOOD)) {
                    if (!allowFood.getValue())
                        return false;

                    break;
                }

                if (!allowMisc.getValue())
                    return false;
            }
        }

        return true;
    }

}
