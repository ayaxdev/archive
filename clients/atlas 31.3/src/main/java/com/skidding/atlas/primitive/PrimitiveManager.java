package com.skidding.atlas.primitive;

import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.primitive.argument.Argument;
import com.skidding.atlas.primitive.argument.impl.ClampedNumberArgument;
import com.skidding.atlas.primitive.enums.RegisterAs;
import com.skidding.atlas.primitive.usage.PrimitiveBasedModule;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.SettingManager;
import net.optifine.util.ArrayUtils;

import java.lang.reflect.InvocationTargetException;

public class PrimitiveManager extends Manager<PrimitiveFeature> {

    private static volatile PrimitiveManager primitiveManager;

    public static synchronized PrimitiveManager getSingleton() {
        return primitiveManager == null ? primitiveManager = new PrimitiveManager() : primitiveManager;
    }

    public PrimitiveManager() {
        super(PrimitiveFeature.class);
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException {
        super.postMinecraftLaunch();

        final ModuleManager manager = ModuleManager.getSingleton();

        for (PrimitiveFeature primitiveFeature : getFeatures()) {
            if (!ArrayUtils.contains(primitiveFeature.registerAs, RegisterAs.MODULE))
                continue;

            manager.getMap().put(primitiveFeature.name, new PrimitiveBasedModule(primitiveFeature));
        }
    }

}
