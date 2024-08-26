package com.daniel.datsuzei.module;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.event.impl.KeyPressEvent;
import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.feature.Manager;
import com.daniel.datsuzei.settings.SettingFeature;
import com.daniel.datsuzei.settings.SettingManager;
import com.github.jezevcik.eventbus.Listener;
import com.github.jezevcik.eventbus.annotations.Listen;

import java.util.ArrayList;
import java.util.Collection;

public class ModuleManager extends Manager<ModuleFeature> {

    private static volatile ModuleManager moduleManager;

    public static ModuleManager getSingleton() {
        if(moduleManager == null)
            moduleManager = new ModuleManager();

        return moduleManager;
    }

    public ModuleManager() {
        super(ModuleFeature.class);
    }

    @Override
    public void preMinecraftLaunch() {
        super.preMinecraftLaunch();

        DatsuzeiClient.getSingleton().getEventBus().subscribe(this);
    }

    @Listen
    public final Listener<KeyPressEvent> keyPressEventListener = keyPressEvent -> {
        this.map.values().stream().filter(moduleFeature -> moduleFeature.getKey() == keyPressEvent.getKey()).forEach(ModuleFeature::toggleEnabled);
    };

    public final Collection<ModuleFeature> getByCategory(ModuleCategory category) {
        final ArrayList<ModuleFeature> moduleFeatures = new ArrayList<>();
        this.map.values().forEach(value -> {
            if (value.getCategory() == category)
                moduleFeatures.add(value);
        });
        return moduleFeatures;
    }


}
