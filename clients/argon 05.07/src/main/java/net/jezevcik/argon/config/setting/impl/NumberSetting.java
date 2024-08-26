package net.jezevcik.argon.config.setting.impl;

import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.setting.Setting;
import net.jezevcik.argon.utils.math.MathUtils;
import net.jezevcik.argon.utils.objects.GenericsUtil;

public abstract class NumberSetting<T extends Number> extends Setting<T> {

    private T value;
    public final T min, max, step;
    public final Class<T> type;

    public NumberSetting(String name, String displayName, T value, T min, T max, T step, Class<T> type, Config parent) {
        super(name, displayName, parent);

        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
        this.type = type;

        if (!(value instanceof Comparable<?>) || !(min instanceof Comparable<?>) || !(step instanceof Comparable<?>))
            throw new IllegalArgumentException("Values must be of a comparable type!");
    }

    public NumberSetting(String name, T value, T min, T max, T step, Class<T> type, Config parent) {
        this(name, name, value, min, max, step, type, parent);
    }

    public abstract void setValue(double value);

    @Override
    public void setValueInternal(T value) {
        final double set = GenericsUtil.cast(Double.class, value);
        final double min = GenericsUtil.cast(Double.class, this.min);
        final double max = GenericsUtil.cast(Double.class, this.max);
        final double step = GenericsUtil.cast(Double.class, this.step);

        final double clamped = MathUtils.clamp(set, min, max);
        final double rounded = MathUtils.roundToStepStrict(clamped, step);

        this.value = GenericsUtil.cast(type, rounded);
    }

    @Override
    public T getValue() {
        return value;
    }

}
