package net.jezevcik.argon.config.setting.impl.number;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.impl.NumberSetting;

public class DoubleSetting extends NumberSetting<Double> {

    // Intellij is saying this unnecessary casting, it is LYING

    public DoubleSetting(String name, String displayName, double value, double min, double max, double step, Config parent) {
        super(name, displayName, (double) value, (double) min, (double) max, (double) step, Double.class, parent);
    }

    public DoubleSetting(String name, double value, double min, double max, double step, Config parent) {
        this(name, name, (double) value, (double) min, (double) max, (double) step, parent);
    }

    @Override
    public void setValue(double value) {
        super.setValue((Double) value);
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
            this.setValueInternal(object.getDoubleValue("value"));
    }
}
