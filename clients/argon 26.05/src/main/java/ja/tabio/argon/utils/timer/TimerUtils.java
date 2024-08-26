package ja.tabio.argon.utils.timer;

public class TimerUtils {

    public static boolean hasTimeElapsed(long from, long to) {
        return getTimeElapsed(from) <= to;
    }

    public static long getTimeElapsed(long from) {
        return System.currentTimeMillis() - from;
    }

}
