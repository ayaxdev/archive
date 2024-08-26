package lord.daniel.alexander.util.math.random.impl;

import com.github.kilianB.pcg.sync.PcgRR;
import com.github.kilianB.pcg.sync.PcgRS;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class PcgRSAlgorithm extends RandomizationAlgorithm {

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
