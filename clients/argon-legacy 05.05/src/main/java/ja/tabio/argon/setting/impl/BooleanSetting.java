package ja.tabio.argon.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {

    public boolean value;

    public BooleanSetting(String name, String displayName, boolean value) {
        super(name, displayName);
        this.value = value;
    }

    public BooleanSetting(String name, boolean value) {
        super(name, name);
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", getValue());
        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        setValue(jsonObject.getBoolean("value"));
    }

    @Override
    public String getSettingIdentifier() {
        return String.format("%s.boolean.%s", owner.getSettingIdentifier(), name);
    }
}
