package lord.daniel.alexander.settings.impl.mode;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;
import lord.daniel.alexander.util.math.random.impl.*;

/**
 * Written by Daniel. on 11/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class RandomizationAlgorithmValue extends StringModeValue {

    public RandomizationAlgorithmValue(String name, AbstractModule owner) {
        this(name, owner, "SecureRandom");
    }

    public RandomizationAlgorithmValue(String name, AbstractModule owner, String value) {
        super(name, owner, value, new String[]{"Random", "SecureRandom", "PcgRR", "PcgRS", "PcgRSFast", "MersenneTwister"});

        this.setValueChangeListeners((setting, oldValue, newValue) -> randomizationAlgorithm = switch (newValue) {
            case "Random" -> new RandomAlgorithm();
            case "PcgRR" -> new PcgRRAlgorithm();
            case "PcgRS" -> new PcgRSAlgorithm();
            case "PcgRSFast" -> new PcgRSFastAlgorithm();
            case "MersenneTwister" -> new MersenneTwisterAlgorithm();
            default -> new SecureRandomAlgorithm();
        });
    }

    @Getter
    private RandomizationAlgorithm randomizationAlgorithm = new SecureRandomAlgorithm();

}
