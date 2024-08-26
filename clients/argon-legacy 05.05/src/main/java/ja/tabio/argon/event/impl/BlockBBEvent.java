package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockBBEvent extends Event {
    public final BlockPos blockPos;
    public final Block block;
    public final AxisAlignedBB boundingBox;
    public final double x, y, z;

    public BlockBBEvent(Block block, BlockPos blockPos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = blockPos;
        this.boundingBox = boundingBox;

        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }
}
