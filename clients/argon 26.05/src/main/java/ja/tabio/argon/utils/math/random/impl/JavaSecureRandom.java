package ja.tabio.argon.utils.math.random.impl;

import java.security.SecureRandom;

public class JavaSecureRandom implements RandomizationBase {

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