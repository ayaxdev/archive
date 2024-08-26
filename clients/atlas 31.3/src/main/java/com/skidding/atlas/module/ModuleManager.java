package com.skidding.atlas.module;


import com.skidding.atlas.event.impl.input.keyboard.KeyPressEvent;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.keybind.KeybindingFeature;
import com.skidding.atlas.keybind.KeybindingManager;
import io.github.racoondog.norbit.EventHandler;
import io.github.racoondog.norbit.EventPriority;

import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager extends Manager<ModuleFeature> {

    private static volatile ModuleManager moduleManager;

    public static synchronized ModuleManager getSingleton() {
        return moduleManager == null ? moduleManager = new ModuleManager() : moduleManager;
    }

    public ModuleManager() {
        super(ModuleFeature.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onKeyPress(KeyPressEvent keyPressEvent) {
        this.map.values().stream().filter(moduleFeature -> {
            final KeybindingFeature keybinding = KeybindingManager.getSingleton().getByName(STR."Module:\{moduleFeature.name}");
            final int key = keybinding.key;
            return key == keyPressEvent.key && !keybinding.binding;
        }).forEach(ModuleFeature::toggleEnabled);
    }

    public final List<ModuleFeature> getByCategory(ModuleCategory moduleCategory) {
        return this.getFeatures().stream().filter(moduleFeature -> moduleFeature.moduleCategory == moduleCategory).collect(Collectors.toList());
    }

}