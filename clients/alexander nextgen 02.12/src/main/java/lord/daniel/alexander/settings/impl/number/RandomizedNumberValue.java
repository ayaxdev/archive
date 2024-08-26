package lord.daniel.alexander.settings.impl.number;

import lombok.Getter;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.mode.RandomizationAlgorithmValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.util.math.MathUtil;
import lord.daniel.alexander.util.math.time.MSTimer;
import lord.daniel.alexander.util.math.time.TimeUtil;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Written by Daniel. on 11/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
public class RandomizedNumberValue<T extends Number> extends AbstractSetting<T> {

    private final T min, max;
    private final int decimalPlaces;

    private final MSTimer recalculationTimer = new MSTimer();

    private final ArrayList<AbstractSetting<?>> values = new ArrayList<>();

    public RandomizedNumberValue(String name, AbstractModule owner, T minValue, T maxValue, T min, T max, int decimalPlaces) {
        super(name, owner, null);
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;

        values.add(new NumberValue<T>("MinRand" + name, owner, minValue, min, max, decimalPlaces));
        values.add(new NumberValue<T>("MaxRand" + name, owner, maxValue, min, max, decimalPlaces));
        values.add(new RandomizationAlgorithmValue(name + "RandomizationAlgorithm", owner).addVisibleCondition(() -> (values.get(0).getValue() != values.get(1).getValue())));
        values.add(new NumberValue<Long>(name + "RecalculationTime", owner, 0L, 0L, 5000L, 0).addVisibleCondition(() -> (values.get(0).getValue() != values.get(1).getValue())));
    }

    @Override
    public T getValue() {
        final Number minVal = this.min;
        final Number maxVal = this.max;

        if (minVal.equals(maxVal)) {
            setValue((T) (Number) minVal);
            return super.getValue();
        }

        final NumberValue<Long> recalculationTimeValue = (NumberValue<Long>) values.get(3);

        if (recalculationTimer.hasReached(recalculationTimeValue.getValue())) {
            recalculationTimer.reset();

            final NumberValue<T> minNumVal = (NumberValue<T>) values.get(0);
            final NumberValue<T> maxNumVal = (NumberValue<T>) values.get(1);
            final RandomizationAlgorithmValue randomizationAlgorithmValue = (RandomizationAlgorithmValue) values.get(2);

            boolean fixed = false;

            double min = minNumVal.getValue().doubleValue();
            double max = maxNumVal.getValue().doubleValue();

            if (min > max) {
                min = max;
            }

            if (max < 0) {
                fixed = true;
                min += Math.abs(max);
                max += Math.abs(max);
            }

            T randomValue;
            if (minVal instanceof Float) {
                randomValue = (T) (Number) (randomizationAlgorithmValue.getRandomizationAlgorithm().getRandomFloat((float) min, (float) max) - (fixed ? Math.abs(maxNumVal.getValue().floatValue()) : 0));
            } else if (minVal instanceof Integer) {
                randomValue = (T) (Number) (randomizationAlgorithmValue.getRandomizationAlgorithm().getRandomInteger((int) min, (int) max) - (fixed ? Math.abs(maxNumVal.getValue().intValue()) : 0));
            } else if (minVal instanceof Long) {
                randomValue = (T) (Number) (randomizationAlgorithmValue.getRandomizationAlgorithm().getRandomLong((long) min, (long) max) - (fixed ? Math.abs(maxNumVal.getValue().longValue()) : 0));
            } else {
                randomValue = (T) (Number) (randomizationAlgorithmValue.getRandomizationAlgorithm().getRandomDouble(min, max) - (fixed ? Math.abs(maxNumVal.getValue().doubleValue()) : 0));
            }

            this.setValue(randomValue);
        }

        return super.getValue();

    }

    public long getCPSValue() {
        return getCPSValue(0);
    }

    public long getCPSValue(final double additional) {
        final NumberValue<T> minVal = (NumberValue<T>) values.get(0);
        final NumberValue<T> maxVal = (NumberValue<T>) values.get(1);

        double min = minVal.getValue().doubleValue(),
                max = maxVal.getValue().doubleValue() > 10 ? maxVal.getValue().doubleValue() + 2 : maxVal.getValue().doubleValue();

        min += additional;
        max += additional;

        if(min > max)
            min = max;

        return TimeUtil.randomClickDelay(getRandomizationAlgorithmValue().getRandomizationAlgorithm(), min, max);
    }

    public RandomizationAlgorithmValue getRandomizationAlgorithmValue() {
        return (RandomizationAlgorithmValue) values.get(2);
    }

    @Override
    public void setValue(T object) {
        Number newNumber = MathUtil.round(object.doubleValue(), decimalPlaces);

        if (this.getMin() instanceof Integer) super.setValue((T) Integer.valueOf(newNumber.intValue()));
        else if (this.getMin() instanceof Float) super.setValue((T) Float.valueOf(newNumber.floatValue()));
        else if (this.getMin() instanceof Long) super.setValue((T) Long.valueOf(newNumber.longValue()));
        else if (this.getMin() instanceof Byte) super.setValue((T) Byte.valueOf(newNumber.byteValue()));
        else if (this.getMin() instanceof Short) super.setValue((T) Short.valueOf(newNumber.shortValue()));
        else if (this.getMin() instanceof Double) super.setValue((T) Double.valueOf(newNumber.doubleValue()));

        if(super.getValue() instanceof Integer) {
            int curr = super.getValue().intValue();
            int min = getMin().intValue();
            int max = getMax().intValue();
            if(curr < min) {
                super.setValue(getMin());
            } else if(curr > max) {
                super.setValue(getMax());
            }
        } else if(super.getValue() instanceof Float) {
            float curr = super.getValue().floatValue();
            float min = getMin().floatValue();
            float max = getMax().floatValue();
            if(curr < min) {
                super.setValue(getMin());
            } else if(curr > max) {
                super.setValue(getMax());
            }
        } else if(super.getValue() instanceof Long) {
            long curr = super.getValue().longValue();
            long min = getMin().longValue();
            long max = getMax().longValue();
            if(curr < min) {
                super.setValue(getMin());
            } else if(curr > max) {
                super.setValue(getMax());
            }
        } else if(super.getValue() instanceof Short) {
            short curr = super.getValue().shortValue();
            short min = getMin().shortValue();
            short max = getMax().shortValue();
            if(curr < min) {
                super.setValue(getMin());
            } else if(curr > max) {
                super.setValue(getMax());
            }
        } else if(super.getValue() instanceof Double) {
            double curr = super.getValue().doubleValue();
            double min = getMin().doubleValue();
            double max = getMax().doubleValue();
            if(curr < min) {
                super.setValue(getMin());
            } else if(curr > max) {
                super.setValue(getMax());
            }
        } else if(super.getValue() instanceof Byte) {
            byte curr = super.getValue().byteValue();
            byte min = getMin().byteValue();
            byte max = getMax().byteValue();
            if(curr < min) {
                super.setValue(getMin());
            } else if(curr > max) {
                super.setValue(getMax());
            }
        }
    }

    @Override
    public <O extends AbstractSetting<T>> O addVisibleCondition(Supplier<Boolean> visible) {
        this.visible.add(visible);
        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addVisibleCondition(visible);
        }
        return (O) this;
    }

    @Override
    public <O extends AbstractSetting<T>> O addExpandableParents(ExpandableValue... expandableValues) {
        super.addExpandableParents(expandableValues);
        for(AbstractSetting<?> abstractSetting : values) {
            abstractSetting.addExpandableParents(expandableValues);
        }
        return (O) this;
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
        if(this.getValue() instanceof Integer) {
            return String.valueOf(this.getValue().intValue());
        } else if(this.getValue() instanceof Double) {
            return String.valueOf(this.getValue().doubleValue());
        } else if(this.getValue() instanceof Float) {
            return String.valueOf(this.getValue().floatValue());
        } else if(this.getValue() instanceof Long) {
            return String.valueOf(this.getValue().longValue());
        }
        return String.valueOf(this.getValue().intValue());
    }
}
