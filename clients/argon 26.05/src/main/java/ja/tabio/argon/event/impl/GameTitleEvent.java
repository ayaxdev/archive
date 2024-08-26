package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class GameTitleEvent extends Event {
    public String title;

    public GameTitleEvent(String title) {
        this.title = title;
    }
}
