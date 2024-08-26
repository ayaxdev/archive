package ja.tabio.argon.utils.math.random.impl;

import com.github.kilianB.pcg.sync.PcgRR;

public class PcgRRRandom implements RandomizationBase {

    private final PcgRR pcgRR = new PcgRR();

    @Override
    public double nextDouble() {
        return pcgRR.nextDouble();
    }

    @Override
    public float nextFloat() {
        return pcgRR.nextFloat();
    }

    @Override
    public int nextInt() {
        return pcgRR.nextInt();
    }

    @Override
    public boolean nextBoolean() {
        return pcgRR.nextBoolean();
    }

}