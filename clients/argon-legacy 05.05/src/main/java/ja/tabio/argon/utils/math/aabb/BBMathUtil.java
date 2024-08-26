package ja.tabio.argon.utils.math.aabb;

import de.florianmichael.rclasses.math.Arithmetics;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class BBMathUtil {

    public static Vec3 getClosestPoint(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(Arithmetics.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX),
                Arithmetics.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY),
                Arithmetics.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

}
