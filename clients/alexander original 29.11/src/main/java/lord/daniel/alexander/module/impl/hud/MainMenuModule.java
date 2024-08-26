package lord.daniel.alexander.module.impl.hud;

import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.storage.impl.BackgroundStorage;
import lord.daniel.alexander.util.render.shader.shaders.ShaderBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "MainMenu", enumModuleType = EnumModuleType.HUD)
public class MainMenuModule extends AbstractModule {

    public final StringModeValue shaderMode;
    private final Map<String, BackgroundStorage.Background> backgroundMap = new HashMap<>();

    public MainMenuModule() {
        List<String> shaderModes = new ArrayList<>();

        for(BackgroundStorage.Background background : BackgroundStorage.getBackgroundStorage().getList()) {
            shaderModes.add(background.name());
            backgroundMap.put(background.name(), background);
        }

        shaderMode = new StringModeValue("Shader", this, "Abraxas", shaderModes);
    }

    public ShaderBackground getBackground() {
        return backgroundMap.get(shaderMode.getValue()).shaderBackground();
    }

    @Override
    public void onEnable() {

    }


    @Override
    public void onDisable() {

    }

}
