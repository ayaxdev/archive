package lord.daniel.alexander.util.math.random;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public abstract class RandomizationAlgorithm {

    public abstract double nextDouble();

    public abstract float nextFloat();

    public abstract int nextInt();

    public abstract boolean nextBoolean();

    public double getRandomDouble(final double min, final double max) {
        return min + (nextDouble() * (max - min));
    }

    public float getRandomFloat(final float min, final float max) {
        return min + (nextFloat() * (max - min));
    }

    public int getRandomInteger(final int min, final int max) {
        return Math.round(getRandomFloat(min, max));
    }

    public long getRandomLong(final long min, final long max) {
        return Math.round(getRandomFloat(min, max));
    }

}
