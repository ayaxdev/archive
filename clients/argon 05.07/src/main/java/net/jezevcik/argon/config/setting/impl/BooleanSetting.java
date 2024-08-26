package net.jezevcik.argon.config.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {

    private boolean value;

    public BooleanSetting(String name, String displayName, boolean value, Config parent) {
        super(name, displayName, parent);
        this.value = value;
    }

    public BooleanSetting(String name, boolean value, Config parent) {
        super(name, name, parent);
        this.value = value;
    }

    @Override
    public void setValueInternal(Boolean newValue) {
        this.value = newValue;
    }

    @Override
    public Boolean getValue() {
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
            this.value = object.getBooleanValue("value");
    }
}
