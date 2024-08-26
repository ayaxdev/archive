package lord.daniel.alexander.util.world;

import lombok.Getter;
import lord.daniel.alexander.interfaces.IMinecraft;
import net.minecraft.util.BlockPos;

public class WorldUtil implements IMinecraft {

    @Getter
    private static final WorldUtil worldUtil = new WorldUtil();

    public double calculateDistance(BlockPos pos1, BlockPos pos2) {
        double deltaX = pos1.getX() - pos2.getX();
        double deltaY = pos1.getY() - pos2.getY();
        double deltaZ = pos1.getZ() - pos2.getZ();

        // Calculate Euclidean distance using the Pythagorean theorem
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

}
