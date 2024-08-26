package lord.daniel.alexander.util.math.random.impl;

import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;

import java.security.SecureRandom;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class SecureRandomAlgorithm extends RandomizationAlgorithm {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public double nextDouble() {
        return secureRandom.nextDouble();
    }

    @Override
    public float nextFloat() {
        return secureRandom.nextFloat();
    }

    @Override
    public int nextInt() {
        return secureRandom.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return secureRandom.nextBoolean();
    }

}
