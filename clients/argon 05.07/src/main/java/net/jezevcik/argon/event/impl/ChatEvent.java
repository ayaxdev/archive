package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;

public class ChatEvent extends Cancellable {

    public String message;

    public ChatEvent(String message) {
        this.message = message;
    }
}
