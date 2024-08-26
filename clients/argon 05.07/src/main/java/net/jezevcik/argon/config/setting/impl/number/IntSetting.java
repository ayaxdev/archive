package net.jezevcik.argon.config.setting.impl.number;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.impl.NumberSetting;
import net.jezevcik.argon.utils.math.MathUtils;

import java.util.Map;

public class IntSetting extends NumberSetting<Integer> {

    public IntSetting(String name, String displayName, int value, int min, int max, int step, Config parent) {
        super(name, displayName, value, min, max, step, Integer.class, parent);
    }

    public IntSetting(String name, int value, int min, int max, int step, Config parent) {
        this(name, name, value, min, max, step, parent);
    }

    @Override
    public void setValue(double value) {
        super.setValue((Integer) (int) Math.round(value));
    }

    @Override
    public JSONObject getData() {
        final JSONObject returnObject = new JSONObject();
        returnObject.put("value", getValue());
        return returnObject;
    }

    @Override
    public void setData(JSONObject object) {
        if (object.containsKey("value"))
            this.setValueInternal(object.getIntValue("value"));
    }
}
