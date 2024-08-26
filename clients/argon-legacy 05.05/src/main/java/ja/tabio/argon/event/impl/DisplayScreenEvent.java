package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class DisplayScreenEvent extends Event {
    public GuiScreen guiScreen;

    public DisplayScreenEvent(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }
}
