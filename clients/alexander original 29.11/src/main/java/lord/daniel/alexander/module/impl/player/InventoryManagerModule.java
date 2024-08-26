package lord.daniel.alexander.module.impl.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.GuiHandleEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.number.RandomizedNumberValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;
import lord.daniel.alexander.util.math.random.impl.SecureRandomAlgorithm;
import lord.daniel.alexander.util.math.time.TimeHelper;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.Arrays;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@ModuleData(name = "InventoryManager", aliases = {"InvManager"}, enumModuleType = EnumModuleType.PLAYER)
public class InventoryManagerModule extends AbstractModule {
    private final BooleanValue openInventory = new BooleanValue("OpenInventory", this, true);

    private final ExpandableValue delaySettings = new ExpandableValue("Delay", this);
    private final RandomizedNumberValue<Long> startDelay = new RandomizedNumberValue<>("StartDelay", this, 250L, 250L, 0L, 1000L, 0).addExpandableParents(delaySettings);
    private final RandomizedNumberValue<Long> throwDelay = new RandomizedNumberValue<>("ThrowDelay", this, 250L, 250L, 0L, 1000L, 0).addExpandableParents(delaySettings);

    private final BooleanValue preferSwords = new BooleanValue("PreferSwords", this, true),
            keepTools = new BooleanValue("KeepTools", this, true);

    private final ExpandableValue slotSettings = new ExpandableValue("Slot", this);
    private final NumberValue<Integer> weaponSlot = new NumberValue<Integer>("WeaponSlot", this, 1, 0, 9, 0).addExpandableParents(slotSettings);
    private final NumberValue<Integer> bowSlot = new NumberValue<Integer>("BowSlot", this, 2, 0, 9, 0).addExpandableParents(slotSettings);
    private final NumberValue<Integer> pickaxeSlot = new NumberValue<Integer>("PickaxeSlot", this, 0, 0, 9, 0).addExpandableParents(slotSettings);
    private final NumberValue<Integer> axeSlot = new NumberValue<Integer>("AxeSlot", this, 0, 0, 9, 0).addExpandableParents(slotSettings);
    private final NumberValue<Integer> shovelSlot = new NumberValue<Integer>("ShovelSlot", this, 0, 0, 9, 0).addExpandableParents(slotSettings);

    private RandomizationAlgorithm randomizationAlgorithm = new SecureRandomAlgorithm();
    private final List<Item> trashItems = Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner, Items.fishing_rod);
    private AutoArmorModule autoArmorModule;
    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper throwTimer = new TimeHelper();

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        setSuffix(openInventory.getValue() ? "OpenInv" : "Blatant");
    };

    @EventLink
    public final Listener<GuiHandleEvent> guiHandleEventListener = guiHandleEvent -> {
        if(autoArmorModule == null)
            autoArmorModule = ModuleStorage.getModuleStorage().getByClass(AutoArmorModule.class);
        if (Methods.mc.currentScreen instanceof GuiInventory) {
            if (!timeHelper.hasReached(startDelay.getValue())) {
                throwTimer.reset();
                return;
            }
        } else {
            timeHelper.reset();
            if (openInventory.getValue())
                return;
        }

        if (autoArmorModule.isEnabled() && !autoArmorModule.isFinished()) {
            timeHelper.reset();
            throwTimer.reset();
            if (openInventory.getValue())
                return;
        }

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                long throwDelay = this.throwDelay.getValue();
                if (throwTimer.hasReached((long) (throwDelay))) {
                    if (weaponSlot.getValue() != 0 && (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) && is == bestWeapon() && Methods.mc.thePlayer.inventoryContainer.getInventory().contains(bestWeapon()) && Methods.mc.thePlayer.inventoryContainer.getSlot((int) (35 + weaponSlot.getValue())).getStack() != is && !preferSwords.getValue()) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, (int) (weaponSlot.getValue() - 1), 2, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (weaponSlot.getValue() != 0 && is.getItem() instanceof ItemSword && is == bestSword() && Methods.mc.thePlayer.inventoryContainer.getInventory().contains(bestSword()) && Methods.mc.thePlayer.inventoryContainer.getSlot((int) (35 + weaponSlot.getValue())).getStack() != is && preferSwords.getValue()) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, (int) (weaponSlot.getValue() - 1), 2, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (bowSlot.getValue() != 0 && is.getItem() instanceof ItemBow && is == bestBow() && Methods.mc.thePlayer.inventoryContainer.getInventory().contains(bestBow()) && Methods.mc.thePlayer.inventoryContainer.getSlot((int) (35 + bowSlot.getValue())).getStack() != is) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, (int) (bowSlot.getValue() - 1), 2, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (pickaxeSlot.getValue() != 0 && is.getItem() instanceof ItemPickaxe && is == bestPick() && is != bestWeapon() && keepTools.getValue() && Methods.mc.thePlayer.inventoryContainer.getInventory().contains(bestPick()) && Methods.mc.thePlayer.inventoryContainer.getSlot((int) (35 + pickaxeSlot.getValue())).getStack() != is) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, (int) (pickaxeSlot.getValue() - 1), 2, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (axeSlot.getValue() != 0 && is.getItem() instanceof ItemAxe && is == bestAxe() && is != bestWeapon() && keepTools.getValue() && Methods.mc.thePlayer.inventoryContainer.getInventory().contains(bestAxe()) && Methods.mc.thePlayer.inventoryContainer.getSlot((int) (35 + axeSlot.getValue())).getStack() != is) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, (int) (axeSlot.getValue() - 1), 2, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (shovelSlot.getValue() != 0 && is.getItem() instanceof ItemSpade && is == bestShovel() && is != bestWeapon() && keepTools.getValue() && Methods.mc.thePlayer.inventoryContainer.getInventory().contains(bestShovel()) && Methods.mc.thePlayer.inventoryContainer.getSlot((int) (35 + shovelSlot.getValue())).getStack() != is) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, (int) (shovelSlot.getValue() - 1), 2, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (trashItems.contains(is.getItem()) || isBadStack(is)) {
                        getPlayerController().windowClick(Methods.mc.thePlayer.inventoryContainer.windowId, i, 1, 4, Methods.mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
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

    public boolean isBadStack(ItemStack is) {
        if ((is.getItem() instanceof ItemSword) && is != bestWeapon() && !preferSwords.getValue())
            return true;
        if (is.getItem() instanceof ItemSword && is != bestSword() && preferSwords.getValue())
            return true;
        if (is.getItem() instanceof ItemBow && is != bestBow())
            return true;
        if (keepTools.getValue()) {
            if (is.getItem() instanceof ItemAxe && is != bestAxe() && (preferSwords.getValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && is != bestPick() && (preferSwords.getValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemSpade && is != bestShovel())
                return true;
        } else {
            if (is.getItem() instanceof ItemAxe && (preferSwords.getValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && (preferSwords.getValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemSpade)
                return true;
        }
        return false;
    }

    public ItemStack bestWeapon() {
        ItemStack bestWeapon = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) {
                    float toolDamage = getItemDamage(is);
                    if (toolDamage >= itemDamage) {
                        itemDamage = getItemDamage(is);
                        bestWeapon = is;
                    }
                }
            }
        }

        return bestWeapon;
    }

    public ItemStack bestSword() {
        ItemStack bestSword = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    float swordDamage = getItemDamage(is);
                    if (swordDamage >= itemDamage) {
                        itemDamage = getItemDamage(is);
                        bestSword = is;
                    }
                }
            }
        }

        return bestSword;
    }

    public ItemStack bestBow() {
        ItemStack bestBow = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBow) {
                    float bowDamage = getBowDamage(is);
                    if (bowDamage >= itemDamage) {
                        itemDamage = getBowDamage(is);
                        bestBow = is;
                    }
                }
            }
        }

        return bestBow;
    }

    public ItemStack bestAxe() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemAxe) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    public ItemStack bestPick() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPickaxe) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    public ItemStack bestShovel() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = getPlayer().inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSpade) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    public float getToolRating(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, false);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack) * 2.00F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;
        return damage;
    }

    public float getItemDamage(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, true);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;

        if (itemStack.getItem() instanceof ItemSword)
            damage += 0.2;
        return damage;
    }

    public float getBowDamage(ItemStack itemStack) {
        float damage = 5;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.75F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += itemStack.getMaxDamage() - itemStack.getItemDamage() * 0.001F;
        return damage;
    }

    public float getToolMaterialRating(ItemStack itemStack, boolean checkForDamage) {
        final Item is = itemStack.getItem();
        float rating = 0;

        if (is instanceof ItemSword) {
            switch (((ItemSword) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 4;
                    break;
                case "STONE":
                    rating = 5;
                    break;
                case "IRON":
                    rating = 6;
                    break;
                case "EMERALD":
                    rating = 7;
                    break;
            }
        } else if (is instanceof ItemPickaxe) {
            switch (((ItemPickaxe) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 2;
                    break;
                case "STONE":
                    rating = 3;
                    break;
                case "IRON":
                    rating = checkForDamage ? 4 : 40;
                    break;
                case "EMERALD":
                    rating = checkForDamage ? 5 : 50;
                    break;
                default:
                    break;
            };
        } else if (is instanceof ItemAxe) {
            switch (((ItemAxe) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 3;
                    break;
                case "STONE":
                    rating = 4;
                    break;
                case "IRON":
                    rating = 5;
                    break;
                case "EMERALD":
                    rating = 6;
                    break;
                default:
                    break;
            };
        } else if (is instanceof ItemSpade) {
            switch (((ItemSpade) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 1;
                    break;
                case "STONE":
                    rating = 2;
                    break;
                case "IRON":
                    rating = 3;
                    break;
                case "EMERALD":
                    rating = 4;
                    break;
                default:
                    break;
            };
        }

        return rating;
    }
}
