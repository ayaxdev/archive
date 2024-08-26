package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class SendPitchEvent extends Event {

    public float pitch;

    public SendPitchEvent(float pitch) {
        this.pitch = pitch;
    }
}
