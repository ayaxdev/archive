package com.atani.nextgen.util.reflection;

import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

@UtilityClass
public class ReflectionUtil {

    public boolean hasParameterlessConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }

    public boolean classExists(String path) {
        try {
            Class.forName(path);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}