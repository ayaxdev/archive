package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;

public class ChatEvent extends Event {
    public String message;

    public ChatEvent(String message) {
        this.message = message;
    }
}
