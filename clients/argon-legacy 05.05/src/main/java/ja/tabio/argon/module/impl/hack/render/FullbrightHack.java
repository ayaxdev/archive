package ja.tabio.argon.module.impl.hack.render;

import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;

@ModuleData(name = "FullBright", category = ModuleCategory.HACK)
@HackData(hackCategory = HackCategory.RENDER)
public class FullbrightHack extends Module {

    private float lastBrightness;

    @Override
    public void onEnable() {
        lastBrightness = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 200;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = lastBrightness;
    }

}
