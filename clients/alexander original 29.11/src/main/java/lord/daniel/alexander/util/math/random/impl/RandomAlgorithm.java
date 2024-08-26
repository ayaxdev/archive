package lord.daniel.alexander.util.math.random.impl;

import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class RandomAlgorithm extends RandomizationAlgorithm {

    private final Random random = new Random();

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }

    @Override
    public float nextFloat() {
        return random.nextFloat();
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

}
