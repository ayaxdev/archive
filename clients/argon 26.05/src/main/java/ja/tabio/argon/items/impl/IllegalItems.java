package ja.tabio.argon.items.impl;

import ja.tabio.argon.items.ClientItemGroup;
import ja.tabio.argon.items.annotation.RegisterItemGroup;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

@RegisterItemGroup
public class IllegalItems extends ClientItemGroup {

    public IllegalItems() {
        super("IllegalItems");
    }

    @Override
    protected ItemStack getIcon() {
        return new ItemStack(Items.LINGERING_POTION);
    }

    @Override
    protected void addEntries(ItemGroup.Entries entries) {
        // Items that not normally accessible from the creative menu
        entries.add(new ItemStack(Blocks.COMMAND_BLOCK));
        entries.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
        entries.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
        entries.add(new ItemStack(Items.COMMAND_BLOCK_MINECART));
        entries.add(new ItemStack(Blocks.END_PORTAL_FRAME));
        entries.add(new ItemStack(Blocks.DRAGON_EGG));
        entries.add(new ItemStack(Blocks.BARRIER));
        entries.add(new ItemStack(Blocks.JIGSAW));
        entries.add(new ItemStack(Blocks.STRUCTURE_BLOCK));
        entries.add(new ItemStack(Blocks.STRUCTURE_VOID));
        entries.add(new ItemStack(Blocks.SPAWNER));
        entries.add(new ItemStack(Items.DEBUG_STICK));

        // Potions
        entries.add(createSplashPotion("Devious Potion", Registries.STATUS_EFFECT.streamEntries().map(statusEffect -> new StatusEffectInstance(statusEffect, Integer.MAX_VALUE, 127)).toList()));
        entries.add(createSplashPotion("Kill Potion", List.of(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 0, 125),
                new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 0, 125))));

        // Tools
        entries.add(createItem("Good sword", Items.DIAMOND_SWORD,
                new Enchantment[]{
                        Enchantments.BANE_OF_ARTHROPODS,
                        Enchantments.VANISHING_CURSE,
                        Enchantments.FIRE_ASPECT,
                        Enchantments.KNOCKBACK,
                        Enchantments.LOOTING,
                        Enchantments.MENDING,
                        Enchantments.SHARPNESS,
                        Enchantments.SMITE,
                        Enchantments.SWEEPING_EDGE,
                        Enchantments.UNBREAKING
                },
                new int[] {
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                }));
        entries.add(createItem("Gooder sword", Items.NETHERITE_SWORD,
                new Enchantment[]{
                        Enchantments.BANE_OF_ARTHROPODS,
                        Enchantments.VANISHING_CURSE,
                        Enchantments.FIRE_ASPECT,
                        Enchantments.KNOCKBACK,
                        Enchantments.LOOTING,
                        Enchantments.MENDING,
                        Enchantments.SHARPNESS,
                        Enchantments.SMITE,
                        Enchantments.SWEEPING_EDGE,
                        Enchantments.UNBREAKING
                },
                new int[] {
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                }));

        entries.add(createItem("Good Pickaxe", Items.DIAMOND_PICKAXE,
                new Enchantment[]{
                        Enchantments.BANE_OF_ARTHROPODS,
                        Enchantments.VANISHING_CURSE,
                        Enchantments.FORTUNE,
                        Enchantments.MENDING,
                        Enchantments.UNBREAKING,
                        Enchantments.EFFICIENCY
                },
                new int[] {
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                }));
        entries.add(createItem("Gooder Pickaxe", Items.NETHERITE_PICKAXE,
                new Enchantment[]{
                        Enchantments.BANE_OF_ARTHROPODS,
                        Enchantments.VANISHING_CURSE,
                        Enchantments.FORTUNE,
                        Enchantments.MENDING,
                        Enchantments.UNBREAKING,
                        Enchantments.EFFICIENCY
                },
                new int[] {
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                }));

        // Gotta have 'em knockback sticks
        entries.add(createItem("Knockback Stick", Items.STICK,
                new Enchantment[]{Enchantments.KNOCKBACK}, new int[] {Integer.MAX_VALUE}));
        entries.add(createItem("Humble Knockback Stick", Items.STICK,
                new Enchantment[]{Enchantments.KNOCKBACK}, new int[] {5}));
        entries.add(createItem("Normal Knockback Stick", Items.STICK,
                new Enchantment[]{Enchantments.KNOCKBACK}, new int[] {2}));
    }

    private ItemStack createSplashPotion(String name, List<StatusEffectInstance> effects) {
        final ItemStack itemStack = new ItemStack(Items.SPLASH_POTION);

        itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name));
        itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), effects));

        return itemStack;
    }

    private ItemStack createItem(String name, ItemConvertible itemConvertible, Enchantment[] enchantments, int[] levels) {
        if(enchantments.length != levels.length)
            throw new IllegalArgumentException("Invalid enchantment length");

        final ItemStack itemStack = new ItemStack(itemConvertible);
        final ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

        for (int i = 0; i < enchantments.length; i++) {
            builder.add(enchantments[i], levels[i]);
        }

        itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name));
        itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());

        return itemStack;
    }
}
