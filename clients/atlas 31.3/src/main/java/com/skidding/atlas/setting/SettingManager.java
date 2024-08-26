package com.skidding.atlas.setting;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.module.ModuleManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class SettingManager extends Manager<SettingFeature<?>> {

    private static volatile SettingManager singleton;

    public static SettingManager getSingleton() {
        return singleton == null ? singleton = new SettingManager() : singleton;
    }

    public SettingManager() {
        super(null);
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException {
        add(new ArrayList<>(ModuleManager.getSingleton().getFeatures()));


        super.postMinecraftLaunch();
    }
    
    public void add(Feature... features) {
        this.add(Arrays.asList(features));
    }
    
    public void add(List<Feature> features) {
        for(Feature feature : features) {
            Class<? extends Feature> moduleClass = feature.getClass();
            for(Field settingField : moduleClass.getFields()) {
                try {
                    settingField.setAccessible(true);
                    if(settingField.getType().equals(SettingFeature.class)) {
                        SettingFeature<?> settingFeature = (SettingFeature<?>) settingField.get(feature);
                        add(STR."\{feature.getName()}:\{settingFeature.getName()}", settingFeature);
                    }
                } catch (IllegalAccessException illegalAccessException) {
                    AtlasClient.getInstance().logger.error(STR."Couldn't access field \{settingField.getName()}", illegalAccessException);
                }
            }
        }
    }

    public void remove(Feature... features) {
        for(final Feature feature : features) {
            final List<String> names = new ArrayList<>();

            this.map.forEach((key, _) -> {
                if (key.startsWith(STR."\{feature.getName()}:"))
                    names.add(key);
            });

            for(String s : names) {
                this.map.remove(s);
            }
        }
    }

    public Collection<SettingFeature<?>> getByOwner(Feature feature) {
        final ArrayList<SettingFeature<?>> settings = new ArrayList<>();
        this.map.forEach((key, value) -> {
            if (key.startsWith(STR."\{feature.getName()}:"))
                settings.add(value);
        });
        return settings;
    }

}
