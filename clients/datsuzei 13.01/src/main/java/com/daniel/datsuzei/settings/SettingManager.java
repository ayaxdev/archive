package com.daniel.datsuzei.settings;

import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.feature.Manager;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.module.ModuleManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class SettingManager extends Manager<SettingFeature<?>> {

    private static volatile SettingManager settingManager;

    public static SettingManager getSingleton() {
        if(settingManager == null)
            settingManager = new SettingManager();

        return settingManager;
    }

    @Override
    public void postMinecraftLaunch() throws IllegalAccessException {
        for(ModuleFeature moduleFeature : ModuleManager.getSingleton().getFeatures()) {
            Class<? extends ModuleFeature> moduleClass = moduleFeature.getClass();
            for(Field settingField : moduleClass.getFields()) {
                settingField.setAccessible(true);
                Object settingObject = settingField.get(moduleFeature);
                if(settingObject instanceof SettingFeature<?> settingFeature) {
                    add(STR."\{moduleFeature.getName()}:\{settingFeature.getName()}", settingFeature);
                }
            }
        }
    }

    public final Collection<SettingFeature<?>> getByOwner(Feature feature) {
        final ArrayList<SettingFeature<?>> settings = new ArrayList<>();
        this.map.forEach((key, value) -> {
            if (key.startsWith(STR."\{feature.getName()}:"))
                settings.add(value);
        });
        return settings;
    }

}
