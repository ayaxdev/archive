package ja.tabio.argon.utils.math.random.impl;

import com.github.kilianB.pcg.sync.PcgRS;

public class PcgRSRandom implements RandomizationBase {

    private final PcgRS pcgRS = new PcgRS();

    @Override
    public double nextDouble() {
        return pcgRS.nextDouble();
    }

    @Override
    public float nextFloat() {
        return pcgRS.nextFloat();
    }

    @Override
    public int nextInt() {
        return pcgRS.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return pcgRS.nextBoolean();
    }

}