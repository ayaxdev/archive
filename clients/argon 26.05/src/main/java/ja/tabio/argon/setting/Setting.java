package ja.tabio.argon.setting;

import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Nameable;
import ja.tabio.argon.setting.interfaces.Settings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Setting<T> implements Nameable, Settings, Identifiable {

    public final Map<String, Object> data = new LinkedHashMap<>();

    private Supplier<Boolean> visibility = () -> true;
    protected ChangeListener<T> changeListener = (pre, oldValue, newValue) -> {};

    public final String name, displayName;

    public Setting(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;

        register();
    }

    @SuppressWarnings("unchecked")
    public <V extends Setting<T>> V visibility(Supplier<Boolean> supplier) {
        this.visibility = supplier;
        return (V) this;
    }

    public <V extends Setting<T>> V visibility(Setting<?> setting, Object value) {
        this.visibility = () -> setting.getValue().equals(value);
        return (V) this;
    }

    public <V extends Setting<T>> V visibility(Setting<?> setting) {
        this.visibility = () -> setting.getValue().equals(true);
        return (V) this;
    }

    public <V extends Setting<T>> V change(ChangeListener<T> changeListener) {
        this.changeListener = changeListener;
        return (V) this;
    }

    public boolean visibility() {
        return visibility.get();
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getUniqueIdentifier() {
        return String.format("Setting-%s", getName());
    }

    public interface ChangeListener<T> {

        void onChange(boolean pre, T oldValue, T newValue);

    }

}
