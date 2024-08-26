package net.jezevcik.argon.utils.objects;

import com.google.common.primitives.Primitives;

/**
 * A set of methods used for interacting with generics.
 */
@SuppressWarnings("unchecked")
public class GenericsUtil {

    /**
     * Casts a generic Number of one type, to a different type.
     *
     * @param numberClass The class of the desired type,
     * @param value The value which shall be cast.
     * @return The cast value.
     * @param <T> The desired type.
     * @param <V> The type of the original value.
     */
    public static <T extends Number, V extends Number> T cast(Class<T> numberClass, final V value) {
        numberClass = Primitives.wrap(numberClass);

        Object casted;

        if (numberClass == Byte.class) {
            casted = value.byteValue();
        } else if (numberClass == Short.class) {
            casted = value.shortValue();
        } else if (numberClass == Integer.class) {
            casted = value.intValue();
        } else if (numberClass == Long.class) {
            casted = value.longValue();
        } else if (numberClass == Float.class) {
            casted = value.floatValue();
        } else {
            if (numberClass != Double.class) {
                throw new ClassCastException(String.format("%s cannot be casted to %s", value.getClass(), numberClass));
            }
            casted = value.doubleValue();
        }

        return (T) casted;
    }

}
