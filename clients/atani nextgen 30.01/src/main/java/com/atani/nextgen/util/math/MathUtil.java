package com.atani.nextgen.util.math;

public class MathUtil {

    public static int[] range(int min, int max) {
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

}
