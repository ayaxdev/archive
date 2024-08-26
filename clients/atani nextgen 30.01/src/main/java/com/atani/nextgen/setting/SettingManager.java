package com.atani.nextgen.setting;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.feature.Feature;
import com.atani.nextgen.feature.Manager;
import com.atani.nextgen.module.ModuleManager;
import com.atani.nextgen.processor.ProcessorManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class SettingManager extends Manager<SettingFeature<?>> {

    private static volatile SettingManager singleton;

    public static SettingManager getSingleton() {
        if(singleton == null)
            singleton = new SettingManager();

        return singleton;
    }

    public SettingManager() {
        super(null);
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException {
        final ArrayList<Feature> features = new ArrayList<>(ModuleManager.getSingleton().getFeatures());
        //features.addAll(ProcessorManager.getSingleton().getFeatures());

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
                    AtaniClient.getInstance().logger.error(STR."Couldn't access field \{settingField.getName()}", illegalAccessException);
                }
            }
        }

        super.postMinecraftLaunch();
    }

    public final Collection<SettingFeature<?>> getByOwner(Feature feature) {
        final ArrayList<SettingFeature<?>> settings = new ArrayList<>();
        this.map.forEach((key, value) -> {
            if (key.startsWith(STR."\{feature.getName().toLowerCase()}:"))
                settings.add(value);
        });
        return settings;
    }

}
