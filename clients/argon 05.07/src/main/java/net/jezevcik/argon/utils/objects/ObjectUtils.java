package net.jezevcik.argon.utils.objects;

public class ObjectUtils {

    public static <T extends Enum<?>> T getEnumByString(Class<T> clazz, String value) {
        for(T type : clazz.getEnumConstants()) {
            if(type.toString().equals(value))
                return type;
        }

        throw new IllegalArgumentException("Unable to found an enum constant");
    }

    public static <T> T getByString(T[] values, String value) {
        for(T type : values) {
            if(type.toString().equals(value)) {
                return type;
            }

        }

        throw new IllegalArgumentException("Unable to found an entry");
    }


}
