package ja.tabio.argon.utils.math.random.impl;

import net.geoi.util.MTRandom;

public class MersenneTwisterRandom implements RandomizationBase {

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