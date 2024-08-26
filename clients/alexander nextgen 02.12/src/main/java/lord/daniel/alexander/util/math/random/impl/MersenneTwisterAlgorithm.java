package lord.daniel.alexander.util.math.random.impl;

import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;
import net.geoi.util.MTRandom;

import java.util.Random;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class MersenneTwisterAlgorithm extends RandomizationAlgorithm {

    private final MTRandom mtRandom = new MTRandom();

    @Override
    public double nextDouble() {
        return mtRandom.nextDouble();
    }

    @Override
    public float nextFloat() {
        return mtRandom.nextFloat();
    }

    @Override
    public int nextInt() {
        return mtRandom.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return mtRandom.nextBoolean();
    }

}
