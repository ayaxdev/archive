package lord.daniel.alexander.settings.impl.number;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.util.math.MathUtil;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
public class NumberValue<T extends Number> extends AbstractSetting<T> {

    private final T min, max, increment;
    private final int decimalPlaces;
    private final boolean forceClamped;

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, T increment, int decimalPlaces, boolean forceClamped) {
        super(name, owner, value);
        this.min = min;
        this.max = max;
        this.increment = increment == null ? (T) MathUtil.castNumber(value, MathUtil.getNumberWithDecimalPlaces(1, decimalPlaces)) : increment;
        this.decimalPlaces = decimalPlaces;
        this.forceClamped = forceClamped;
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, T increment, boolean forceClamped) {
        this(name, owner, value, min, max, increment, (value instanceof Integer || value instanceof Long) ? 0 : 1, forceClamped);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, T increment, int decimalPlaces) {
        this(name, owner, value, min, max, increment, decimalPlaces, true);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, T increment) {
        this(name, owner, value, min, max, increment, (value instanceof Integer || value instanceof Long) ? 0 : 1, true);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, boolean forceClamped) {
        this(name, owner, value, min, max, null, (value instanceof Integer || value instanceof Long) ? 0 : 1, forceClamped);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, int decimalPlaces) {
        this(name, owner, value, min, max, null, decimalPlaces, true);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max) {
        this(name, owner, value, min, max, null, (value instanceof Integer || value instanceof Long) ? 0 : 1, true);
    }

    @Override
    public void setValue(T object) {
        double tempNumber = MathUtil.roundToIncrement(object.doubleValue(), increment.doubleValue());
        tempNumber = MathUtil.round(tempNumber, decimalPlaces);

        T newNumber = (T) MathUtil.castNumber(getValue(), tempNumber);

        if (forceClamped) {
            if (getValue() instanceof Number) {
                Number curr = getValue();
                Number min = getMin();
                Number max = getMax();

                if (curr.doubleValue() < min.doubleValue()) {
                    newNumber = (T) min;
                } else if (curr.doubleValue() > max.doubleValue()) {
                    newNumber = (T) max;
                }
            }
        }

        super.setValue(newNumber);
    }

    @Override
    public void setValueByString(String valueString) {
        if(this.getValue() instanceof Integer) {
            this.setValue((T) Integer.valueOf(valueString));
        } else if(this.getValue() instanceof Double) {
            this.setValue((T) Double.valueOf(valueString));
        } else if(this.getValue() instanceof Float) {
            this.setValue((T) Float.valueOf(valueString));
        } else if(this.getValue() instanceof Long) {
            this.setValue((T) Long.valueOf(valueString));
        }
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(this.getValue());
    }

}
