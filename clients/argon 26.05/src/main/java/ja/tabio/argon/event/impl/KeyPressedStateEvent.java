package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.client.option.KeyBinding;

public class KeyPressedStateEvent extends Event {
    public KeyBinding keyBinding;
    public boolean pressed;

    public KeyPressedStateEvent(KeyBinding keyBinding, boolean pressed) {
        this.keyBinding = keyBinding;
        this.pressed = pressed;
    }
}