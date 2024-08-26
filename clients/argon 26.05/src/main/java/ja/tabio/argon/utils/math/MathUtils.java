package ja.tabio.argon.utils.math;

import ja.tabio.argon.interfaces.Minecraft;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MathUtils implements Minecraft {

    public static double coerceIn(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int floorToNearestMultiple(int x, int n) {
        return n * (int) Math.floor((double) x / (double) n);
    }

    public static class AABB {
        public static Vec3d closestPointToBox(Vec3d start, Box box) {
            double x = Math.max(box.minX, Math.min(box.maxX, start.x));
            double y = Math.max(box.minY, Math.min(box.maxY, start.y));
            double z = Math.max(box.minZ, Math.min(box.maxZ, start.z));
            return new Vec3d(x, y, z);
        }

        public static Vec3d getBestAimPoint(Box box) {
            assert mc.player != null;

            final Vec3d start = mc.player.getEyePos();

            if (box.minX < start.x && start.x < box.maxX &&
                    box.minZ < start.z && start.z < box.maxZ) {
                double x = box.minX + (box.maxX - box.minX) / 2.0;
                double y = Math.max(box.minY, Math.min(box.maxY, start.y));
                double z = box.minZ + (box.maxZ - box.minZ) / 2.0;
                return new Vec3d(x, y, z);
            }

            return closestPointToBox(start, box);
        }
    }

}
