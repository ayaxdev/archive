package com.skidding.atlas.module.impl.world;

import com.skidding.atlas.event.impl.game.gui.ScreenClickEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChestStealerModule extends ModuleFeature {

    public final SettingFeature<Boolean> instant = check("Instant", false).build();
    public final SettingFeature<Float> startDelay = slider("Start delay", 150, 0, 500, 0)
            .addDependency(instant, false).build();
    public final SettingFeature<Float> grabDelay = slider("Grab delay", 150, 0, 500, 0)
            .addDependency(instant, false).build();

    public final SettingFeature<Boolean> randomPick = check("Random pick", true).build();
    public final SettingFeature<Boolean> autoClose = check("Auto close", true).build();
    public final SettingFeature<Boolean> nameCheck = check("Name check", true).build();

    final TimerUtil startTimer = new TimerUtil(), grabTimer = new TimerUtil();
    final List<Integer> itemsToSteal = new ArrayList<>();

    public ChestStealerModule() {
        super(new ModuleBuilder("ChestStealer", "Steals from chests", ModuleCategory.WORLD));
    }

    @EventHandler
    public void onGuiHandle(ScreenClickEvent screenClickEvent) {
        if (mc.currentScreen == null) {
            startTimer.reset();
            grabTimer.reset();
            return;
        }

        if (mc.currentScreen instanceof GuiChest) {
            if (!instant.getValue() && !startTimer.hasElapsed(startDelay.getValue().longValue(), false))
                return;

            itemsToSteal.clear();

            final ContainerChest chest = (ContainerChest) getPlayer().openContainer;
            final IInventory inventory = chest.getLowerChestInventory();

            boolean isEmpty = true;

            if (inventory.hasCustomName() && !inventory.getName().equalsIgnoreCase("Chest") && nameCheck.getValue())
                return;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                final ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null) {
                    itemsToSteal.add(i);
                }
            }

            if (randomPick.getValue())
                Collections.shuffle(itemsToSteal);

            for (int i : itemsToSteal) {
                final ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null) {
                    if (!instant.getValue() && !grabTimer.hasElapsed(grabDelay.getValue().longValue(), false))
                        return;

                    mc.playerController.windowClick(chest.windowId, i, 0, 1, getPlayer());

                    grabTimer.reset();
                    isEmpty = false;
                }
            }

            if (isEmpty && autoClose.getValue())
                getPlayer().closeScreen();
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

}
