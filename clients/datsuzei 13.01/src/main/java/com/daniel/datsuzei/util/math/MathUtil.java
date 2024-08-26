package com.daniel.datsuzei.util.math;

import de.florianmichael.rclasses.math.Arithmetics;

public class MathUtil extends Arithmetics {

    public static int max(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("No values provided");
        }

        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }

        return max;
    }

    public static int min(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("No values provided");
        }

        int min = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }

        return min;
    }

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
