package ja.tabio.argon.utils.math.random.impl;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface RandomizationBase {

    double nextDouble();

    float nextFloat();

    int nextInt();

    boolean nextBoolean();

    default double getRandomDouble(final double min, final double max) {
        return min + (nextDouble() * (max - min));
    }

    default float getRandomFloat(final float min, final float max) {

        return min + (nextFloat() * (max - min));
    }

    default int getRandomInteger(final int min, final int max) {
        return Math.round(getRandomFloat(min, max));
    }

    default String getRandomString(int length) {
        return IntStream.range(0, length)
                .mapToObj(operand -> String.valueOf((char) getRandomInteger('a', 'z')))
                .collect(Collectors.joining());
    }

}