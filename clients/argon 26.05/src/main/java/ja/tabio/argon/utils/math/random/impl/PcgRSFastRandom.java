package ja.tabio.argon.utils.math.random.impl;

import com.github.kilianB.pcg.fast.PcgRSFast;

public class PcgRSFastRandom implements RandomizationBase {

    private final PcgRSFast pcgRSFast = new PcgRSFast();

    @Override
    public double nextDouble() {
        return pcgRSFast.nextDouble();
    }

    @Override
    public float nextFloat() {
        return pcgRSFast.nextFloat();
    }

    @Override
    public int nextInt() {
        return pcgRSFast.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return pcgRSFast.nextBoolean();
    }

}