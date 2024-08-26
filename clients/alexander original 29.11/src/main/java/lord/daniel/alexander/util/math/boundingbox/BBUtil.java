package lord.daniel.alexander.util.math.boundingbox;

import lombok.experimental.UtilityClass;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class BBUtil {

    public Vec3 getNearestPointBB(Vec3 eye, AxisAlignedBB box) {
        double[] origin = { eye.xCoord, eye.yCoord, eye.zCoord };
        double[] destMins = { box.minX, box.minY, box.minZ };
        double[] destMaxs = { box.maxX, box.maxY, box.maxZ };

        for (int i = 0; i <= 2; i++) {
            if (origin[i] > destMaxs[i]) {
                origin[i] = destMaxs[i];
            } else if (origin[i] < destMins[i]) {
                origin[i] = destMins[i];
            }
        }

        return new Vec3(origin[0], origin[1], origin[2]);
    }


}
