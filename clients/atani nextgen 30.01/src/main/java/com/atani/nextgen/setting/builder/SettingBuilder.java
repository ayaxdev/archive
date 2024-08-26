package com.atani.nextgen.setting.builder;

import com.atani.nextgen.setting.SettingFeature;

import java.util.*;

public abstract class SettingBuilder<T> {

    protected String description = "No description provided!";

    protected final Map<SettingFeature<?>, Object> dependencies = new LinkedHashMap<>();
    public final List<SettingFeature.ValueChangeListener<T>> valueChangeListeners = new ArrayList<>();
    public final List<SettingFeature.ValueChangeOverride<T>> valueChangeOverrides = new ArrayList<>();

    public SettingBuilder<T> withDescription(String description) {
        if(description.contains(".") || !description.contains("?"))
            throw new RuntimeException("GO KILL YOURSELF");

        this.description = description;
        return this;
    }

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
