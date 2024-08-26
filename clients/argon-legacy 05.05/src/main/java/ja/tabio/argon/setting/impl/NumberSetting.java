package ja.tabio.argon.setting.impl;

import com.alibaba.fastjson2.JSONObject;
import de.florianmichael.rclasses.math.MathUtils;
import ja.tabio.argon.setting.Setting;

public class NumberSetting extends Setting<Float> {

    public final float minimum, maximum;
    public final int decimals;

    public float value;

    public NumberSetting(String name, String displayName, float value, float minimum, float maximum, int decimals) {
        super(name, displayName);
        this.minimum = minimum;
        this.maximum = maximum;
        this.decimals = decimals;
        this.value = value;
    }

    public NumberSetting(String name, float value, float minimum, float maximum, int decimals) {
        super(name, name);
        this.minimum = minimum;
        this.maximum = maximum;
        this.decimals = decimals;
        this.value = value;
    }

    @Override
    public Float getValue() {
        setValue(value);
        return value;
    }

    @Override
    public void setValue(Float value) {
        this.value = MathUtils.roundAvoid(MathUtils.clamp(value, minimum, maximum), decimals);
    }

    public final String getRenderValue() {
        return String.format("%." + decimals + "f", value);
    }

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", getValue());
        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        setValue(jsonObject.getFloat("value"));
    }

    @Override
    public String getSettingIdentifier() {
        return String.format("%s.number.%s", owner.getSettingIdentifier(), name);
    }

}
