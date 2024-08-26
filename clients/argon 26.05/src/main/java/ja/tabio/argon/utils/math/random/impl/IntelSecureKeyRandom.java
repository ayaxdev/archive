package ja.tabio.argon.utils.math.random.impl;

import net.nullschool.util.DigitalRandom;

public class IntelSecureKeyRandom implements RandomizationBase {

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