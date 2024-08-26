package ja.tabio.argon.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.setting.Setting;
import org.apache.commons.lang3.ArrayUtils;

public class ModeSetting extends Setting<String> {

    public String value;
    public final String[] modes;

    public ModeSetting(String name, String displayName, String value, String[] modes) {
        super(name, displayName);
        this.value = value;
        this.modes = modes;
    }

    public ModeSetting(String name, String value, String... modes) {
        super(name, name);
        this.value = value;
        this.modes = modes;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if (ArrayUtils.contains(modes, value))
            this.value = value;
        else
            this.value = modes[0];
    }

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", getValue());
        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        setValue(jsonObject.getString("value"));
    }

    @Override
    public String getSettingIdentifier() {
        return String.format("%s.mode.%s", owner.getSettingIdentifier(), name);
    }

}
