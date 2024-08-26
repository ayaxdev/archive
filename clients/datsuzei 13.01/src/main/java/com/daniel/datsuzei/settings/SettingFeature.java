package com.daniel.datsuzei.settings;

import com.daniel.datsuzei.feature.Feature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class SettingFeature<T> implements Feature {

    private final String name;

    public abstract void setValue(T value);

    public abstract T getValue();

}
