package ja.tabio.argon.processor;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.interfaces.Nameable;

public class Processor implements Identifiable, Nameable, Minecraft {

    public final String name;

    public Processor(final String name) {
        this.name = name;
    }

    public void initialize() {
        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public String getUniqueIdentifier() {
        return String.format("Processor-%s", getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
