package net.jezevcik.argon.config.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.Setting;
import org.apache.commons.lang3.ArrayUtils;

public class ModeSetting extends Setting<String> {

    public final String[] modes;
    private String value;

    public ModeSetting(String name, String displayName, Object value, Object[] modes, Config parent) {
        super(name, displayName, parent);

        this.value = value.toString();

        this.modes = new String[modes.length];

        for (int i = 0; i < modes.length; i++) {
            this.modes[i] = modes[i].toString();
        }
    }

    public ModeSetting(String name, Object value, Object[] modes, Config parent) {
        this(name, name, value, modes, parent);
    }

    @Override
    public void setValueInternal(String newValue) {
        if (ArrayUtils.contains(modes, value))
            this.value = newValue;
        else
            throw new IllegalArgumentException("Value not in modes!");
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
