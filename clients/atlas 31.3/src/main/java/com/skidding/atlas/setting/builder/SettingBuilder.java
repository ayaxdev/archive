package com.skidding.atlas.setting.builder;

import com.skidding.atlas.setting.SettingFeature;

import java.util.*;

public abstract class SettingBuilder<T> {

    protected final Map<SettingFeature<?>, Object> dependencies = new LinkedHashMap<>();
    public final List<SettingFeature.ValueChangeListener<T>> valueChangeListeners = new ArrayList<>();
    public final List<SettingFeature.ValueChangeOverride<T>> valueChangeOverrides = new ArrayList<>();

    public SettingBuilder<T> addDependency(SettingFeature<?> dependency) {
        return addDependency(dependency, true);
    }

    public SettingBuilder<T> addDependency(SettingFeature<?> dependency, Object object) {
        this.dependencies.put(dependency, object);

        return this;
    }

    @SafeVarargs
    public final SettingBuilder<T> addValueChangeListeners(SettingFeature.ValueChangeListener<T>... valueChangeListeners) {
        this.valueChangeListeners.addAll(Arrays.asList(valueChangeListeners));

        return this;
    }

    @SafeVarargs
    public final SettingBuilder<T> addValueChangeOverrides(SettingFeature.ValueChangeOverride<T>... valueChangeOverrides) {
        this.valueChangeOverrides.addAll(Arrays.asList(valueChangeOverrides));

        return this;
    }

    public abstract SettingFeature<T> build();

}
