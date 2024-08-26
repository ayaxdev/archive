package lord.daniel.alexander.util.math.random.impl;

import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;
import net.nullschool.util.DigitalRandom;

import java.util.Random;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class IntelSecureKeyAlgorithm extends RandomizationAlgorithm {

    private final DigitalRandom digitalRandom = new DigitalRandom();

    @Override
    public double nextDouble() {
        return digitalRandom.nextDouble();
    }

    @Override
    public float nextFloat() {
        return digitalRandom.nextFloat();
    }

    @Override
    public int nextInt() {
        return digitalRandom.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return digitalRandom.nextBoolean();
    }

}
