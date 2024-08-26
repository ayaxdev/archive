package lord.daniel.alexander.util.math.time;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class TimeHelper {
    private long ms;

    public boolean hasReached(long delay) {
        return System.currentTimeMillis() - ms >= delay;
    }

    public boolean hasReached(double delay) {
        return System.currentTimeMillis() - ms >= delay;
    }

    public boolean hasReached(long lastTime, long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - ms + lastTime) >= currentTime;
    }

    public boolean hasReached(double lastTime, long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - ms + lastTime) >= currentTime;
    }

    public boolean hasReached(long delay, boolean reset) {
        if(hasReached(delay)) {
            if(reset)
                reset();
            return true;
        }
        return false;
    }

    public boolean hasReached(double delay, boolean reset) {
        if(hasReached(delay)) {
            if(reset)
                reset();
            return true;
        }
        return false;
    }

    public boolean hasReached(long delay, long currentTime, boolean reset) {
        if(hasReached(delay, currentTime)) {
            if(reset)
                reset();
            return true;
        }
        return false;
    }

    public boolean hasReached(double delay, long currentTime, boolean reset) {
        if(hasReached(delay, currentTime)) {
            if(reset)
                reset();
            return true;
        }
        return false;
    }

    public void reset() {
        ms = System.currentTimeMillis();
    }

    public long getMs() {
        return System.currentTimeMillis() - ms;
    }

    public void setMs(long ms) {
        this.ms = ms;
    }
}