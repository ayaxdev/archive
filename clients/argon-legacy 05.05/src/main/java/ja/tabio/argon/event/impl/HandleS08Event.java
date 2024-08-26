package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import ja.tabio.argon.event.enums.Stage;

public class HandleS08Event extends Event {
    public final Stage stage;
    public float rotationYaw, rotationPitch;

    public HandleS08Event(Stage stage, float rotationYaw, float rotationPitch) {
        this.stage = stage;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
