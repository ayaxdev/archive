package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import ja.tabio.argon.event.enums.Stage;

public class TickEvent extends Event {

    public final Stage stage;

    public TickEvent(Stage stage) {
        this.stage = stage;
    }
}
