package lord.daniel.alexander.util.math.random.impl;

import com.github.kilianB.pcg.fast.PcgRSFast;
import com.github.kilianB.pcg.sync.PcgRR;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class PcgRSFastAlgorithm extends RandomizationAlgorithm {

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
