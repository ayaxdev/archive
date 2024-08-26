package com.skidding.atlas.util.reflection;

import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

@UtilityClass
public class ReflectionUtil {

    public boolean hasParameterlessConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }

}