package com.skidding.atlas.primitive.usage;

import com.skidding.atlas.event.impl.player.update.WalkingPacketsEvent;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.primitive.PrimitiveFeature;
import com.skidding.atlas.primitive.argument.Argument;
import com.skidding.atlas.primitive.argument.impl.ClampedNumberArgument;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.SettingManager;
import io.github.racoondog.norbit.EventHandler;

public class PrimitiveBasedModule extends ModuleFeature {

    private final PrimitiveFeature primitiveFeature;

    public final SettingFeature<String> executionMode;

    public PrimitiveBasedModule(PrimitiveFeature primitiveFeature) {
        super(new ModuleBuilder(primitiveFeature.name, primitiveFeature.description, primitiveFeature.moduleCategory));
        this.primitiveFeature = primitiveFeature;

        for (Argument argument : primitiveFeature.arguments) {
            final String settingName = STR."\{primitiveFeature.name}:\{argument.name}";

            if (argument.type == Boolean.class) {
                SettingManager.getSingleton().getMap().put(settingName, check(argument.name, false).build());
            } else if (argument.type == Integer.class || argument.type == Long.class || argument.type == Float.class) {
                int decimals = argument.type == Integer.class || argument.type == Long.class ? 0 : 1;

                if (argument instanceof ClampedNumberArgument clampedNumberArgument) {
                    SettingManager.getSingleton().getMap().put(settingName, slider(argument.name, clampedNumberArgument.min + clampedNumberArgument.max / 2, clampedNumberArgument.min, clampedNumberArgument.max, decimals).build());
                } else {
                    SettingManager.getSingleton().getMap().put(settingName, slider(argument.name, 100, 0, 1500, decimals).build());
                }
            } else if (argument.type == String.class) {
                SettingManager.getSingleton().getMap().put(settingName, text(argument.name, "").build());
            }
        }

        executionMode = mode("Execution", "On enable", new String[] {"On enable", "Constant"}).build();
        SettingManager.getSingleton().getMap().put(STR."\{primitiveFeature.name}:Execution", executionMode);
    }

    @EventHandler
    public final void onSendPackets(WalkingPacketsEvent walkingPacketsEvent) {
        if(executionMode.getValue().equalsIgnoreCase("Constant")) {
            String[] strings = new String[primitiveFeature.arguments.length];
            for (int i = 0; i < primitiveFeature.arguments.length; i++) {
                strings[i] = SettingManager.getSingleton().getByName(STR."\{name}:\{primitiveFeature.arguments[i].name}").getValue().toString();
            }
            primitiveFeature.execute(primitiveFeature.parseArgs(strings));
        }
    }

    @Override
    protected void onEnable() {
        if(executionMode.getValue().equalsIgnoreCase("On enable")) {
            String[] strings = new String[primitiveFeature.arguments.length];
            for (int i = 0; i < primitiveFeature.arguments.length; i++) {
                strings[i] = SettingManager.getSingleton().getByName(STR."\{name}:\{primitiveFeature.arguments[i].name}").getValue().toString();
            }
            primitiveFeature.execute(primitiveFeature.parseArgs(strings));

            setEnabled(false);
        }
    }

    @Override
    protected void onDisable() {

    }
}
