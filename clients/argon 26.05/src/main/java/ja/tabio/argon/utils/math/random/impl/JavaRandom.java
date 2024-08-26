package ja.tabio.argon.utils.math.random.impl;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class JavaRandom implements RandomizationBase {

    @Override
    public double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public float nextFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    @Override
    public int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

}