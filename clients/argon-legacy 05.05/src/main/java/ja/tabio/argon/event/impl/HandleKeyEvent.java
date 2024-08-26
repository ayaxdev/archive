package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class HandleKeyEvent extends Event {

    public int key;

    public HandleKeyEvent(int key) {
        this.key = key;
    }
}
