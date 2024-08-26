package com.skidding.atlas.module.impl.player;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

import java.util.Comparator;
import java.util.stream.IntStream;

public class AutoToolModule extends ModuleFeature {

    public AutoToolModule() {
        super(new ModuleBuilder("AutoTool", "Automatically equips the most suitable tool for any given task", ModuleCategory.PLAYER));
    }

    @EventHandler
    public void onPlayerPackets(WalkingPacketsEvent walkingPacketsEvent) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) return;

        if (walkingPacketsEvent.eventType == Event.EventType.PRE) {
            BlockPos position = mc.objectMouseOver.getBlockPos();
            if (position == null) return;

            Block block = getWorld().getBlockState(position).getBlock();
            if (block == null) return;

            int slot = IntStream.range(0, 9)
                    .filter(index -> getPlayer().inventory.getStackInSlot(index) != null)
                    .boxed()
                    .max(Comparator.comparingInt(index ->
                            (int) getPlayer().inventory.getStackInSlot(index).getStrVsBlock(block)))
                    .orElse(1000);

            if (slot == 1000) return;

            getPlayer().inventory.currentItem = slot;
        }
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
