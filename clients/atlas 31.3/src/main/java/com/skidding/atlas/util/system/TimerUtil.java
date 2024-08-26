package com.skidding.atlas.util.system;

import lombok.Setter;

@Setter
public class TimerUtil {

    public long lastMS = System.currentTimeMillis();
    public long currentTimeOffset;

    public long getTime() {
        return getCurrentTime() - lastMS;
    }

    public void reset() {
        lastMS = getCurrentTime();
    }

    public boolean hasElapsed(long time, boolean reset) {
        if (getCurrentTime() - lastMS > time) {
            if (reset) {
                reset();
            }

            return true;
        }

        return false;
    }

    public boolean hasElapsed(long time) {
        return getCurrentTime() - lastMS > time;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis() + currentTimeOffset;
    }

}
