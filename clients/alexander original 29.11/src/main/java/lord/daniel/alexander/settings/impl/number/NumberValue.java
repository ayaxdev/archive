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

    private final T min, max;
    private final int decimalPlaces;
    private final boolean forceClamped;

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, int decimalPlaces, boolean forceClamped) {
        super(name, owner, value);
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.forceClamped = forceClamped;
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, boolean forceClamped) {
        this(name, owner, value, min, max, (value instanceof Integer || value instanceof Long) ? 0 : 1, forceClamped);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max, int decimalPlaces) {
        this(name, owner, value, min, max, decimalPlaces, true);
    }

    public NumberValue(String name, AbstractModule owner, T value, T min, T max) {
        this(name, owner, value, min, max, (value instanceof Integer || value instanceof Long) ? 0 : 1, true);
    }

    @Override
    public void setValue(T object) {
        Number newNumber = MathUtil.round(object.doubleValue(), decimalPlaces);

        if (this.getValue() instanceof Integer) super.setValue((T) Integer.valueOf(newNumber.intValue()));
        else if (this.getValue() instanceof Float) super.setValue((T) Float.valueOf(newNumber.floatValue()));
        else if (this.getValue() instanceof Long) super.setValue((T) Long.valueOf(newNumber.longValue()));
        else if (this.getValue() instanceof Byte) super.setValue((T) Byte.valueOf(newNumber.byteValue()));
        else if (this.getValue() instanceof Short) super.setValue((T) Short.valueOf(newNumber.shortValue()));
        else if (this.getValue() instanceof Double) super.setValue((T) Double.valueOf(newNumber.doubleValue()));

        if(forceClamped) {
            if(getValue() instanceof Integer) {
                int curr = getValue().intValue();
                int min = getMin().intValue();
                int max = getMax().intValue();
                if(curr < min) {
                    super.setValue(getMin());
                } else if(curr > max) {
                    super.setValue(getMax());
                }
            } else if(getValue() instanceof Float) {
                float curr = getValue().floatValue();
                float min = getMin().floatValue();
                float max = getMax().floatValue();
                if(curr < min) {
                    super.setValue(getMin());
                } else if(curr > max) {
                    super.setValue(getMax());
                }
            } else if(getValue() instanceof Long) {
                long curr = getValue().longValue();
                long min = getMin().longValue();
                long max = getMax().longValue();
                if(curr < min) {
                    super.setValue(getMin());
                } else if(curr > max) {
                    super.setValue(getMax());
                }
            } else if(getValue() instanceof Short) {
                short curr = getValue().shortValue();
                short min = getMin().shortValue();
                short max = getMax().shortValue();
                if(curr < min) {
                    super.setValue(getMin());
                } else if(curr > max) {
                    super.setValue(getMax());
                }
            } else if(getValue() instanceof Double) {
                double curr = getValue().doubleValue();
                double min = getMin().doubleValue();
                double max = getMax().doubleValue();
                if(curr < min) {
                    super.setValue(getMin());
                } else if(curr > max) {
                    super.setValue(getMax());
                }
            } else if(getValue() instanceof Byte) {
                byte curr = getValue().byteValue();
                byte min = getMin().byteValue();
                byte max = getMax().byteValue();
                if(curr < min) {
                    super.setValue(getMin());
                } else if(curr > max) {
                    super.setValue(getMax());
                }
            }
        }
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
