package lord.daniel.alexander.util.math.time;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class MinecraftTimeUtil {

    public static long convertToMinecraftTime(int hours, int minutes) {
        int hoursConverted = (hours * 1000) - 6000;
        int minutesConverted = (minutes * 10) - 60;
        return (long) minutesConverted + hoursConverted;
    }

}
