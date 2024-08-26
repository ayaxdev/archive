package com.skidding.atlas.util.java.object;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnumUtil {

    public static <T extends Enum<?>> T getEnumConstantBasedOnString(Class<T> clazz, String value) {
        for(T type : clazz.getEnumConstants()) {
            if(type.toString().equals(value))
                return type;
        }

        throw new IllegalArgumentException("Unable to found an enum constant");
    }

}