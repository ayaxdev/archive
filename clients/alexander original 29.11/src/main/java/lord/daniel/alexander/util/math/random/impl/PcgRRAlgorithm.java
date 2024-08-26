package lord.daniel.alexander.util.math.random.impl;

import com.github.kilianB.pcg.sync.PcgRR;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;

import java.util.Random;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class PcgRRAlgorithm extends RandomizationAlgorithm {

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
