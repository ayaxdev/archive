package lord.daniel.alexander.util.world;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class WorldUtil implements Methods {

    public double calculateDistance(BlockPos pos1, BlockPos pos2) {
        double deltaX = pos1.getX() - pos2.getX();
        double deltaY = pos1.getY() - pos2.getY();
        double deltaZ = pos1.getZ() - pos2.getZ();

        // Calculate Euclidean distance using the Pythagorean theorem
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public Block getBlockUnderPlayer() {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock();
    }

}
