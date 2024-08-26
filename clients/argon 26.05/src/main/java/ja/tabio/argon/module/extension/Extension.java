package ja.tabio.argon.module.extension;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.interfaces.Nameable;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.group.SettingGroup;

import java.util.List;

public class Extension extends SettingGroup implements Identifiable, Nameable, Minecraft {

    public final boolean boundToModule;

    public Module parent;

    public Extension(String name, Module parent, boolean boundToModule) {
        super(name, parent);

        this.parent = parent;
        this.boundToModule = boundToModule;
    }

    public void onModuleEnable() {
        if (boundToModule) {
            enable();
        }
    }

    public void onModuleDisable() {
        if (boundToModule) {
            disable();
        }
    }

    public void enable() {
        onEnable();
        Argon.getInstance().eventBus.subscribe(this);
    }

    public void disable() {
        Argon.getInstance().eventBus.unsubscribe(this);
        onDisable();
    }

    public List<Setting<?>> add() { return List.of(); }

    protected void onEnable() { }

    protected void onDisable() { }

    @Override
    public String getUniqueIdentifier() {
        return String.format("Extension-%s-%s", parent.getUniqueIdentifier(), super.getUniqueIdentifier());
    }

    @Override
    public Object getSettingIdentifier() {
        return parent.getSettingIdentifier();
    }

}
