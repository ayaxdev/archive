package ja.tabio.argon.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.setting.Setting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.LinkedList;
import java.util.List;

public class MultiSetting extends Setting<Boolean> {

    public final List<BooleanSetting> settings = new LinkedList<>();

    public MultiSetting(String name, String displayName, final String[] enabled, final String[] modes) {
        super(name, displayName);

        for (String mode : modes) {
            settings.add(new BooleanSetting(mode, mode, ArrayUtils.contains(enabled, mode)));
        }
    }

    public MultiSetting(String name, final String[] enabled, final String[] modes) {
        super(name, name);

        for (String mode : modes) {
            settings.add(new BooleanSetting(mode, mode, ArrayUtils.contains(enabled, mode)));
        }
    }

    public boolean is(String name) {
        for (BooleanSetting booleanSetting : settings) {
            if (booleanSetting.getName().equals(name))
                return booleanSetting.getValue();
        }

        return false;
    }

    @Override
    public void postInit() {
        super.postInit();

        for (BooleanSetting booleanSetting : settings) {
            addSetting(booleanSetting);
        }
    }

    @Override
    public Boolean getValue() {
        for (BooleanSetting booleanSetting : settings)
            if (!booleanSetting.getValue())
                return false;
        return true;
    }

    @Override
    public void setValue(Boolean value) {
        for (BooleanSetting booleanSetting : settings)
            booleanSetting.setValue(value);
    }

    @Override
    public JSONObject serialize() { return new JSONObject(); }

    @Override
    public void deserialize(JSONObject jsonObject) { }

    @Override
    public String getSettingIdentifier() {
        return String.format("%s.mode.%s", owner.getSettingIdentifier(), name);
    }
}
