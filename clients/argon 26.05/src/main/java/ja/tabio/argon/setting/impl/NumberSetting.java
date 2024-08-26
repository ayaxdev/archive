package ja.tabio.argon.setting.impl;

import de.florianmichael.rclasses.math.MathUtils;
import ja.tabio.argon.setting.Setting;

import java.util.Objects;

public class NumberSetting extends Setting<Float> {

    public final float minimum, maximum;
    public final int decimals;

    private float value;

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
        if (Objects.equals(this.value, value))
            return;

        final float oldValue = this.value;
        this.changeListener.onChange(true, oldValue, value);

        this.value = MathUtils.roundAvoid(MathUtils.clamp(value, minimum, maximum), decimals);

        this.changeListener.onChange(false, oldValue, value);
    }

}
