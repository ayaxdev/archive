package com.skidding.atlas.keybind;

import com.skidding.atlas.event.impl.input.keyboard.KeyPressEvent;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.module.ModuleManager;
import io.github.racoondog.norbit.EventHandler;

import java.lang.reflect.InvocationTargetException;

public class KeybindingManager extends Manager<KeybindingFeature> {

    private static volatile KeybindingManager keybindingManager;

    public static synchronized KeybindingManager getSingleton() {
        if(keybindingManager == null)
            keybindingManager = new KeybindingManager();

        return keybindingManager;
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException {
        for(ModuleFeature moduleFeature : ModuleManager.getSingleton().getFeatures()) {
            final String moduleKeybindingName = STR."Module:\{moduleFeature.getName()}";
            final KeybindingFeature keybindingFeature = new KeybindingFeature(moduleKeybindingName, STR."Internal key for the \{moduleFeature.getName()} module");

            keybindingFeature.key = moduleFeature.defaultKey;

            if(moduleFeature.allowedKeys != null)
                for(int allowed : moduleFeature.allowedKeys) {
                    keybindingFeature.allowed.add(allowed);
                }

            if(moduleFeature.disallowedKeys != null)
                for(int disallowed : moduleFeature.disallowedKeys) {
                    keybindingFeature.disallowed.add(disallowed);
                }

            add(moduleKeybindingName, keybindingFeature);
        }

        super.postMinecraftLaunch();
    }

    @EventHandler
    public final void onKeyPress(KeyPressEvent keyPressEvent) {
        final int key = keyPressEvent.key;

        for(KeybindingFeature keybindingFeature : getFeatures()) {
            if(keybindingFeature.binding) {
                if((keybindingFeature.allowed.isEmpty() || keybindingFeature.allowed.contains(key)) && !keybindingFeature.disallowed.contains(key)) {
                    keybindingFeature.key = keyPressEvent.key;
                    keybindingFeature.binding = false;

                    break;
                }
            }
        }
    }

}
