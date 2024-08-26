package ja.tabio.argon.screen.clickgui;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.ISerializable;
import net.minecraft.client.gui.GuiScreen;

public abstract class ClickGui extends GuiScreen implements ISerializable, Argon.IArgonAccess {

    public abstract void reset();

}
