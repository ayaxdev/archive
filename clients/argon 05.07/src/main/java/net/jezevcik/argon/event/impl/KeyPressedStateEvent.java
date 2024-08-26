package net.jezevcik.argon.event.impl;

import net.minecraft.client.option.KeyBinding;

public class KeyPressedStateEvent {

    public final KeyBinding keyBinding;
    public boolean pressed;

    public KeyPressedStateEvent(KeyBinding keyBinding, boolean pressed) {
        this.keyBinding = keyBinding;
        this.pressed = pressed;
    }
}
