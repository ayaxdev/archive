package com.skidding.atlas.event.impl.player.movement;

import com.skidding.atlas.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
public class CalculateCollisionEvent extends Event {
    public Block block;
    public BlockPos blockPos;
    public AxisAlignedBB boundingBox;
}
