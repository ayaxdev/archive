package lord.daniel.alexander.util.math.time;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.util.math.random.RandomizationAlgorithm;

@UtilityClass
public class TimeUtil {

    public long randomClickDelay(RandomizationAlgorithm randomizationAlgorithm, final double minCPS, final double maxCPS) {
        return (long) ((randomizationAlgorithm.nextDouble() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }

}
