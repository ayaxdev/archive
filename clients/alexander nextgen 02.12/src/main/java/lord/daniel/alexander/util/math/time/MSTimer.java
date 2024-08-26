package lord.daniel.alexander.util.math.time;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class MSTimer {

    private long ms;

    public boolean hasReached(long time, boolean reset) {
        if(hasReached(time)) {
            if(reset)
                reset();
            return true;
        }
        return false;
    }

    public boolean hasReached(long time) {
        return System.currentTimeMillis() - ms >= time;
    }

    public void reset() {
        ms = System.currentTimeMillis();
    }



}