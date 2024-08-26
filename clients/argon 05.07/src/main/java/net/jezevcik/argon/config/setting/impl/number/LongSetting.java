package net.jezevcik.argon.config.setting.impl.number;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.impl.NumberSetting;

public class LongSetting extends NumberSetting<Long> {

    public LongSetting(String name, String displayName, long value, long min, long max, long step, Config parent) {
        super(name, displayName, value, min, max, step, Long.class, parent);
    }

    public LongSetting(String name, long value, long min, long max, long step, Config parent) {
        this(name, name, value, min, max, step, parent);
    }

    @Override
    public void setValue(double value) {
        super.setValue((Long) Math.round(value));
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
            this.setValueInternal(object.getLongValue("value"));
    }
}
