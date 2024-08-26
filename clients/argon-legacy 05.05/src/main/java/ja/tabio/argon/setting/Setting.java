package ja.tabio.argon.setting;

import ja.tabio.argon.interfaces.INameable;
import ja.tabio.argon.interfaces.ISerializable;
import ja.tabio.argon.setting.interfaces.ISettings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Setting<T> implements INameable, ISettings, ISerializable {

    public final Map<String, Object> data = new LinkedHashMap<>();

    public Supplier<Boolean> visibility = () -> true;
    public final String name, displayName;
    public ISettings owner;

    public Setting(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public void postInit() {
        register();
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public <V extends Setting<T>> V visibility(Supplier<Boolean> supplier) {
        this.visibility = supplier;
        return (V) this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
