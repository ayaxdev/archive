package net.jezevcik.argon.config.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.Setting;

public class TextSetting extends Setting<String> {

    private String value;

    public TextSetting(String name, String displayName, String value, Config parent) {
        super(name, displayName, parent);
        this.value = value;
    }

    public TextSetting(String name, String value, Config parent) {
        super(name, name, parent);
        this.value = value;
    }

    @Override
    public void setValueInternal(String newValue) {
        this.value = newValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public JSONObject getData() {
        final JSONObject returnObject = new JSONObject();
        returnObject.put("value", value);
        return returnObject;
    }

    @Override
    public void setData(JSONObject object) {
        if (object.containsKey("value"))
            this.value = object.getString("value");
    }
}
