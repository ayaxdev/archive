package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.client.gui.screen.Screen;

public class WorldResetScreenEvent extends Event {
    public Screen screen;

    public WorldResetScreenEvent(Screen screen) {
        this.screen = screen;
    }
}
