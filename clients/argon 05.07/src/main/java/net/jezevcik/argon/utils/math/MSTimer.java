package net.jezevcik.argon.utils.math;

public class MSTimer {

    public long ms;

    public MSTimer() {
        reset();
    }

    public void reset() {
        ms = System.currentTimeMillis();
    }

    public boolean reached(long time) {
        return System.currentTimeMillis() - ms > time;
    }

}
