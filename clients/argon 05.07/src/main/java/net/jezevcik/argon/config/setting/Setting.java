package net.jezevcik.argon.config.setting;

import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.interfaces.ConfigEntry;
import net.jezevcik.argon.config.setting.interfaces.ValueChangeRunnable;
import net.jezevcik.argon.file.interfaces.Savable;
import net.jezevcik.argon.system.identifier.Identifiables;
import net.jezevcik.argon.system.identifier.IdentifierType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class Setting<T> implements ConfigEntry, Savable {

    public final String name, displayName;
    public final Config parent;
    public final String[] group;
    public final Config config;

    private List<ValueChangeRunnable<T>> valueChangeRunnables = new ArrayList<>();
    private List<Supplier<Boolean>> visibilitySuppliers = new ArrayList<>();

    public Setting(String name, String displayName, Config parent) {
        this.name = name;
        this.displayName = displayName;
        this.parent = parent;
        this.group = ArrayUtils.add(parent.getGroup(), "settings");

        (config = parent).add(this);
    }

    public void setValue(T newValue) {
        final AtomicReference<T> reference = new AtomicReference<>(newValue);

        valueChangeRunnables.forEach(listener -> reference.set(listener.override(reference.get(), getValue())));
        valueChangeRunnables.forEach(listener -> listener.onChange(reference.get(), getValue()));

        setValueInternal(reference.get());
    }

    protected abstract void setValueInternal(T newValue);

    public abstract T getValue();

    public <A extends Setting<?>> A visibility(Supplier<Boolean> visibility) {
        this.visibilitySuppliers.add(visibility);

        return (A) this;
    }

    public <A extends Setting<?>> A change(ValueChangeRunnable<T> valueChangeRunnable) {
        this.valueChangeRunnables.add(valueChangeRunnable);

        return (A) this;
    }

    public boolean visible() {
        for (Supplier<Boolean> supplier : visibilitySuppliers)
            if (!supplier.get())
                return false;

        return true;
    }

    @Override
    public String getIdentifier(IdentifierType identifierType) {
        return switch (identifierType) {
            case UNIQUE_SHORT -> name;
            case UNIQUE_NORMAL -> Identifiables.getIdentifier(this, name);
            case DISPLAY -> displayName;
        };
    }

    @Override
    public String[] getGroup() {
        return group;
    }

    @Override
    public Config getConfig() {
        return config;
    }

}
