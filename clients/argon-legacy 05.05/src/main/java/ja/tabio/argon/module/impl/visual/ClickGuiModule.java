package ja.tabio.argon.module.impl.visual;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.Argon;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.annotation.VisualData;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.enums.VisualCategory;
import ja.tabio.argon.screen.clickgui.ClickGui;
import ja.tabio.argon.screen.clickgui.hero.HeroClickGui;
import ja.tabio.argon.screen.clickgui.simple.SimpleClickGui;
import ja.tabio.argon.setting.impl.ModeSetting;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.LinkedHashMap;
import java.util.Map;

@ModuleData(name = "ClickGui", key = Keyboard.KEY_RSHIFT, category = ModuleCategory.VISUAL)
@VisualData(visualCategory = VisualCategory.GUI)
public class ClickGuiModule extends Module {

    public final ModeSetting modeSetting = new ModeSetting("Theme", "Herocode", "Herocode", "Minecraft");

    private final Map<String, Class<? extends ClickGui>> classMap = new LinkedHashMap<>();
    private final Map<String, ClickGui> clickguiMap = new LinkedHashMap<>();

    public ClickGuiModule() {
        classMap.put("Herocode", HeroClickGui.class);
        classMap.put("Minecraft", SimpleClickGui.class);
    }

    @Override
    public void onEnable() {
        if (!clickguiMap.containsKey(modeSetting.getValue())) {
            try {
                clickguiMap.put(modeSetting.getValue(), classMap.get(modeSetting.getValue()).getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                Argon.getInstance().logger.error("Failed to create ClickGui object", e);

                setEnabled(false);
                return;
            }
        }

        mc.displayGuiScreen(clickguiMap.get(modeSetting.getValue()));
        setEnabled(false);
    }

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = super.serialize();
        final JSONObject clickGuisObject = new JSONObject();

        for (Map.Entry<String, ClickGui> clickGuiEntry : clickguiMap.entrySet()) {
            final JSONObject clickGuiObject = clickGuiEntry.getValue().serialize();
            clickGuisObject.put(clickGuiEntry.getKey(), clickGuiObject);
        }

        jsonObject.put("ClickGUIs", clickGuisObject);
        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);

        try {
            if (!jsonObject.containsKey("ClickGUIs"))
                return;

            final JSONObject clickGuisObject = jsonObject.getJSONObject("ClickGUIs");
            for (Map.Entry<String, Class<? extends ClickGui>> entry : classMap.entrySet()) {
                if (clickGuisObject.containsKey(entry.getKey())) {
                    final ClickGui newClickGui = classMap.get(entry.getKey()).getDeclaredConstructor().newInstance();
                    newClickGui.deserialize(clickGuisObject.getJSONObject(entry.getKey()));
                    clickguiMap.put(entry.getKey(), newClickGui);
                }
            }
        } catch (Exception e) {
            Argon.getInstance().logger.error("Failed deserializing Click GUIs", e);
        }
    }

}
