package com.daniel.datsuzei.util.reflection;

import java.util.stream.Stream;

public class ReflectionUtil {

    public static boolean hasParameterlessConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }

}
