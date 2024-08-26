package lord.daniel.alexander.util.math;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil {

    public double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundToIncrement(double number, double increment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("Increment must be greater than zero");
        }

        return Math.round(number / increment) * increment;
    }

    public static double getNumberWithDecimalPlaces(int value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places must be non-negative");
        }

        double factor = Math.pow(10, decimalPlaces);
        return value / factor;
    }

    public Number castNumber(Number template, double newValue) {
        Number newNumber = null;
        if (template instanceof Integer) newNumber = ((int) newValue);
        else if (template instanceof Float) newNumber = ((float) newValue);
        else if (template instanceof Long) newNumber = ((long) newValue);
        else if (template instanceof Byte) newNumber = ((byte) newValue);
        else if (template instanceof Short) newNumber = ((short) newValue);
        else if (template instanceof Double) newNumber = ((double) newValue);
        return newNumber;
    } 
    
    public Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public boolean contains(float x, float y, float left, float bottom, float width, float height) {
        return x >= left && y >= bottom && x < left + width && y < bottom + height;
    }


}
