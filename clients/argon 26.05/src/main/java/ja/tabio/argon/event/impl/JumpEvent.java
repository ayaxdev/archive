package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class JumpEvent extends Event {
    public float yaw;

    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }
}
