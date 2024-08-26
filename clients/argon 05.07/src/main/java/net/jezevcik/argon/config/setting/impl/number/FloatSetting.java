package net.jezevcik.argon.config.setting.impl.number;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.impl.NumberSetting;

@Deprecated
public class FloatSetting extends NumberSetting<Float> {

    public FloatSetting(String name, String displayName, float value, float min, float max, float step, Config parent) {
        super(name, displayName, value, min, max, step, Float.class, parent);
    }

    public FloatSetting(String name, float value, float min, float max, float step, Config parent) {
        this(name, name, value, min, max, step, parent);
    }

    @Override
    public void setValue(double value) {
        super.setValue((Float) (float) value);
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
            this.setValueInternal(object.getFloatValue("value"));
    }
}
