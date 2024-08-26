package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.client.gui.screen.Screen;

public class SetScreenEvent extends Event {
    public Screen screen;

    public SetScreenEvent(Screen screen) {
        this.screen = screen;
    }
}
