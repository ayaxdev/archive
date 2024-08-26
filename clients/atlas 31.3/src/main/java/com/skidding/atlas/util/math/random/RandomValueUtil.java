package com.skidding.atlas.util.math.random;

import com.skidding.atlas.util.math.MathUtil;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class RandomValueUtil {

    public static String getRandomString(int length) {
        String alphanumericCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuv";

        StringBuilder randomString = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(alphanumericCharacters.length());
            char randomChar = alphanumericCharacters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public static double getRandomDouble(float min, float max) {
        return MathUtil.interpolate(min, max, Math.random());
    }

    public static float getRandomFloat(float min, float max) {
        return (float) getRandomDouble(min, max);
    }

    public static int getRandomInt(int min, int max) {
        return (int) getRandomDouble(min, max);
    }

}
