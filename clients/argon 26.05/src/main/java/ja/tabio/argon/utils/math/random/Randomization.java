package ja.tabio.argon.utils.math.random;

import ja.tabio.argon.utils.math.random.impl.*;

public enum Randomization {
    JAVA("Java", new JavaRandom()),
    JAVA_SECURE("JavaSecure", new JavaSecureRandom()),
    INTEL_SECURE_KEY("IntelSecureKey", new IntelSecureKeyRandom()),
    PCG_RR("PcgRR", new PcgRRRandom()),
    PCG_RS("PcgRS", new PcgRSRandom()),
    PCG_RS_FAST("PcgRSFast", new PcgRSFastRandom()),
    MERSENNE_TWISTER("MersenneTwister", new MersenneTwisterRandom());

    public final String name;
    public final RandomizationBase algorithm;

    Randomization(String name, RandomizationBase algorithm) {
        this.name = name;
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return name;
    }
}
