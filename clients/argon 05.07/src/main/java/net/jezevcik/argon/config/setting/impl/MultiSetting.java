package net.jezevcik.argon.config.setting.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.Setting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MultiSetting extends Setting<String[]> {

    public final List<String> modes;
    private List<String> value;

    public MultiSetting(String name, String displayName, String[] value, String[] modes, Config parent) {
        super(name, displayName, parent);

        this.value = new ArrayList<>(Arrays.asList(value));
        this.modes = Arrays.asList(modes);
    }

    public MultiSetting(String name, String[] value, String[] modes, Config parent) {
        this(name, name, value, modes, parent);
    }

    @Override
    public void setValueInternal(String[] newValue) {
        for (String s : newValue)
            if (!modes.contains(s))
                throw new IllegalArgumentException("Value not in modes!");

        this.value = Arrays.asList(newValue);
    }

    @Override
    public String[] getValue() {
        return value.toArray(new String[0]);
    }

    public boolean isEnabled(String value) {
        return this.value.contains(value);
    }

    public void toggle(String value) {
        if (!this.modes.contains(value))
            throw new IllegalArgumentException("Value not in modes!");

        if (this.value.contains(value))
            this.value.remove(value);
        else
            this.value.add(value);
    }

    @Override
    public JSONObject getData() {
        final JSONObject returnObject = new JSONObject();

        final JSONArray enabledArray = new JSONArray();

        enabledArray.addAll(value);

        returnObject.put("enabled", enabledArray);

        return returnObject;
    }

    @Override
    public void setData(JSONObject object) {
        if (!object.containsKey("enabled"))
            return;

        final JSONArray enabledArray = object.getJSONArray("enabled");

        this.value.clear();

        for (int i = 0; i < enabledArray.size(); i++) {
            final String value = enabledArray.getString(i);

            this.value.add(value);
        }
    }
}
