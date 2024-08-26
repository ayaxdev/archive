package net.jezevcik.argon.utils.game;

import net.jezevcik.argon.system.minecraft.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryUtils implements Minecraft {

    public static final Class<? extends Block>[] NOT_IDEAL_BRIDGE_BLOCKS = new Class[]{
            CobwebBlock.class,
            AbstractPressurePlateBlock.class,
            PressurePlateBlock.class,
            CarpetBlock.class,
            SlabBlock.class,
            StairsBlock.class,
            WallBlock.class,
            FluidBlock.class,
            BedBlock.class,
            SnowBlock.class,
            FlowerBlock.class,
            LeverBlock.class,
            ButtonBlock.class,
            LadderBlock.class,
            TorchBlock.class,
            WallTorchBlock.class,
            PlantBlock.class,
    };

    public static long getAmountOfBlocksInInventory(boolean bridgeOnly) {
        return getAmountOfBlocksInInventory(client.player.getInventory().main, bridgeOnly)
                + getAmountOfBlocksInInventory(client.player.getInventory().offHand, bridgeOnly);
    }

    public static long getAmountOfBlocksInInventory(DefaultedList<ItemStack> inventory, boolean bridgeOnly) {
        long amount = 0;

        slots:
        for (final ItemStack itemStack : inventory) {
            if (itemStack.getItem() == null)
                continue;

            final Item item = itemStack.getItem();

            if (!(item instanceof BlockItem blockItem))
                continue;

            if (bridgeOnly) {
                for (Class<? extends Block> itKlass : InventoryUtils.NOT_IDEAL_BRIDGE_BLOCKS) {
                    if (itKlass.isAssignableFrom(blockItem.getBlock().getClass()))
                        continue slots;
                }
            }

            amount += itemStack.getCount();
        }

        return amount;
    }

    public static List<ItemStack> getInInventory(Class<? extends Item> klass) {
        final List<ItemStack> out = new ArrayList<>();

        out.addAll(client.player.getInventory().main.stream().filter(itemStack -> klass.isAssignableFrom(itemStack.getItem().getClass())).toList());
        out.addAll(client.player.getInventory().armor.stream().filter(itemStack -> klass.isAssignableFrom(itemStack.getItem().getClass())).toList());
        out.addAll(client.player.getInventory().offHand.stream().filter(itemStack -> klass.isAssignableFrom(itemStack.getItem().getClass())).toList());

        return out;
    }

    public static double getAttackDamage(ItemStack stack) {
        if (stack.getItem() == null)
            return -1;

        if (!(stack.getItem() instanceof ToolItem toolItem))
            return -1;

        return 4.0 + toolItem.getMaterial().getAttackDamage() + EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack) * 1.25f;
    }

    public static double getEffectiveProtection(ItemStack stack) {
        if (stack.getItem() == null)
            return -1;

        if (!(stack.getItem() instanceof ArmorItem armorItem))
            return -1;

        return armorItem.getProtection() + EnchantmentHelper.getLevel(Enchantments.PROTECTION, stack) * 0.04f;
    }

}
