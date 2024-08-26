package net.jezevcik.argon.event.impl;

import net.jezevcik.argon.event.Cancellable;
import net.minecraft.client.gui.screen.Screen;

public class OpenScreenEvent extends Cancellable {

    public Screen screen;

    public OpenScreenEvent(Screen screen) {
        this.screen = screen;
    }
}
