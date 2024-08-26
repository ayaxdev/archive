package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class StrafeEvent extends Event {
    public float yaw;

    public StrafeEvent(float yaw) {
        this.yaw = yaw;
    }
}