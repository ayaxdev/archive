package com.skidding.atlas.util.math;

import de.florianmichael.rclasses.math.Arithmetics;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil extends Arithmetics {

    public int[] range(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min should be less than or equal to max");
        }

        int length = max - min + 1;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = min + i;
        }

        return result;
    }

    public float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public float max(float... values) {
        if(values.length == 0)
            return 0;

        float max = values[0];

        for(float value : values) {
            max = Math.max(value, max);
        }

        return max;
    }

    public float min(float... values) {
        if(values.length == 0)
            return 0;

        float min = values[0];

        for(float value : values) {
            min = Math.min(value, min);
        }

        return min;
    }

    public long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }

}
