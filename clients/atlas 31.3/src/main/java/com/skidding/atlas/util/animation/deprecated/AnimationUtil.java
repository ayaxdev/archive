package com.skidding.atlas.util.animation.deprecated;

public class AnimationUtil {

    public static long delta;

    public static double move(double target, double current, long delta, float speed) {
        if (delta < 1)
            delta = 1;

        double diff = target - current;

        boolean dir = target > current;

        current += (diff / 50) * (delta * speed) + (dir ? 0.001 : -0.001);
        if (dir)
            if (current > target)
                current = target;
        if (!dir)
            if (current < target)
                current = target;
        return current;
    }


}
