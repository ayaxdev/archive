package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;

public class KeyPressEvent extends Cancellable {

    public final int key, modifiers, keyAction;

    public KeyPressEvent(int key, int modifiers, int keyAction) {
        this.key = key;
        this.modifiers = modifiers;
        this.keyAction = keyAction;
    }

}
