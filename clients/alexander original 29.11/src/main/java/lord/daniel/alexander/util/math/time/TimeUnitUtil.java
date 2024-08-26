package lord.daniel.alexander.util.math.time;

import lombok.experimental.UtilityClass;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class TimeUnitUtil {

    public float getMsFromMinutes(int minutes) {
        return minutes * 60 * 1000;
    }

    public float getMsFromSeconds(int seconds) {
        return seconds * 1000;
    }

    public float getMsFromHours(int hours) {
        return hours * 60 * 60 * 1000;
    }

}
