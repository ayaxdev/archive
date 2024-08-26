package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "AutoSoup", categories = {EnumModuleType.COMBAT, EnumModuleType.GHOST})
public class AutoSoupModule extends AbstractModule {

    private final NumberValue<Float> health = new NumberValue<>("Health", this, 13f, 0f, 20f);
    private final BooleanValue drop = new BooleanValue("Drop", this, true),
        fill = new BooleanValue("Fill", this, true),
        autoOpen = new BooleanValue("AutoOpen", this, true),
        autoClose = new BooleanValue("AutoClose", this, true);

    private int slotOnLastTick;
    private int previousSlot;
    private boolean clicked;
    private boolean dropped;
    
    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        if(mc.thePlayer == null)
            return;

        if (mc.currentScreen == null) {
            if (((mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() == Items.bowl) || this.clicked) && mc.thePlayer.inventory.currentItem == this.slotOnLastTick) {
                this.dropped = true;
                this.clicked = false;
                if (this.drop.getValue()) {
                    mc.thePlayer.dropOneItem(true);
                    return;
                }
            }
            if (this.dropped) {
                this.dropped = false;
                if (this.previousSlot != -1) {
                    mc.thePlayer.inventory.currentItem = this.previousSlot;
                }
                this.previousSlot = -1;
            }
            final int slot = this.getSoup();
            if (slot != -1) {
                if (mc.thePlayer.getHealth() <= this.health.getValue() && !this.clicked) {
                    if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSoup) {
                        mc.rightClickMouse();
                        this.clicked = true;
                    }
                    else {
                        if (this.previousSlot == -1) {
                            this.previousSlot = mc.thePlayer.inventory.currentItem;
                        }
                        mc.thePlayer.inventory.currentItem = slot;
                    }
                }
            }
            else if (this.autoOpen.getValue() && this.fill.getValue()) {
                final int wholeInv = this.getSoupInWholeInventory();
                if (wholeInv != -1) {
                    this.openInventory();
                }
            }
        }
        else if (mc.currentScreen instanceof GuiInventory && this.fill.getValue()) {
            final int emptySoup = this.getEmptySoup();
            if (emptySoup != -1) {
                if (Math.sin(ThreadLocalRandom.current().nextDouble(0.0, 6.283185307179586)) <= 0.5) {
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, emptySoup, 1, 4, mc.thePlayer);
                }
            }
            else {
                final int slot2 = this.getSoupExceptHotbar();
                boolean full = true;
                for (int i = 0; i < 9; ++i) {
                    final ItemStack item = mc.thePlayer.inventory.mainInventory[i];
                    if (item == null) {
                        full = false;
                        break;
                    }
                }
                if (this.autoClose.getValue() && (slot2 == -1 || full)) {
                    mc.thePlayer.closeScreen();
                    mc.displayGuiScreen(null);
                    mc.setIngameFocus();
                    return;
                }
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot2, 0, 1, mc.thePlayer);
            }
        }
        this.slotOnLastTick = mc.thePlayer.inventory.currentItem;  
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void openInventory() {
        mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    }

    public int getSoup() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack item = mc.thePlayer.inventory.mainInventory[i];
            if (item != null && item.getItem() instanceof ItemSoup) {
                return i;
            }
        }
        return -1;
    }

    public int getEmptySoup() {
        if (mc.currentScreen instanceof GuiInventory inventory) {
            for (int i = 36; i < 45; ++i) {
                final ItemStack item = inventory.inventorySlots.getInventory().get(i);
                if (item != null && item.getItem() == Items.bowl) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getSoupExceptHotbar() {
        for (int i = 9; i < mc.thePlayer.inventory.mainInventory.length; ++i) {
            final ItemStack item = mc.thePlayer.inventory.mainInventory[i];
            if (item != null && item.getItem() instanceof ItemSoup) {
                return i;
            }
        }
        return -1;
    }

    public int getSoupInWholeInventory() {
        for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; ++i) {
            final ItemStack item = mc.thePlayer.inventory.mainInventory[i];
            if (item != null && item.getItem() instanceof ItemSoup) {
                return i;
            }
        }
        return -1;
    }
}
