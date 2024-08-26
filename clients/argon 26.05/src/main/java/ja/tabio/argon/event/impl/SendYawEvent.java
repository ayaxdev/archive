package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class SendYawEvent extends Event {
    public float yaw;

    public SendYawEvent(float yaw) {
        this.yaw = yaw;
    }
}
