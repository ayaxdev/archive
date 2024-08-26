package ja.tabio.argon.utils.jvm;

public class ObjectUtils {

    public static <T extends Enum<?>> T getEnum(Class<T> clazz, String value) {
        for(T type : clazz.getEnumConstants()) {
            if(type.toString().equals(value))
                return type;
        }

        throw new IllegalArgumentException("Unable to found an enum constant");
    }

    public static <T> T get(T[] values, String value) {
        for(T type : values) {
            if(type.toString().equals(value)) {
                return type;
            }

        }

        throw new IllegalArgumentException("Unable to found an entry");
    }

}
