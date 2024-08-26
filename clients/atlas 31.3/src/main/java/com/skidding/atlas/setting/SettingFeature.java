package com.skidding.atlas.setting;

import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.setting.builder.SettingBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public abstract class SettingFeature<T> implements Feature {

    @Getter
    private T value;

    public final String name, type;
    public final Map<SettingFeature<?>, Object> dependencies;
    public final List<ValueChangeListener<T>> valueChangeListeners;
    public final List<ValueChangeOverride<T>> valueChangeOverrides;

    @Override
    public String getName() {
        return name;
    }

    public abstract SettingBuilder<T> getBuilder();

    public void setValue(T value) {
        T oldValue = getValue();

        if (!valueChangeOverrides.isEmpty()) {
            if (valueChangeOverrides.size() > 1)
                throw new IllegalStateException(STR. "A setting cannot have more than value change override! Setting:\{ this.getName() }" );

            value = valueChangeOverrides.getFirst().override(this, value, oldValue);
        }

        for (ValueChangeListener<T> valueChangeListener : valueChangeListeners) {
            valueChangeListener.change(this, value, oldValue, true);
        }

        this.value = value;

        for (ValueChangeListener<T> valueChangeListener : valueChangeListeners) {
            valueChangeListener.change(this, value, oldValue, false);
        }
    }

    public boolean isVisible() {
        for (Map.Entry<SettingFeature<?>, Object> entry : dependencies.entrySet()) {
            if (!entry.getKey().getValue().equals(entry.getValue()))
                return false;
        }

        return true;
    }

    public interface ValueChangeListener<T> {

        void change(SettingFeature<T> setting, T newValue, T oldValue, boolean pre);

    }

    public interface ValueChangeOverride<T> {

        T override(SettingFeature<T> settingFeature, T newValue, T oldValue);

    }

}
