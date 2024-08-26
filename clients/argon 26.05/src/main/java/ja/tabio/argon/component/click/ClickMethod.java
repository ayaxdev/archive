package ja.tabio.argon.component.click;

import ja.tabio.argon.interfaces.Minecraft;

public abstract class ClickMethod implements Minecraft {

    public abstract int getClicks(double target);

    public abstract void update(double target);

    public abstract void reset(double target);

}
