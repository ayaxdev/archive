package com.daniel.datsuzei.feature;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.util.interfaces.MinecraftClient;
import com.daniel.datsuzei.util.reflection.ReflectionUtil;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class Manager<T extends Feature> implements MinecraftClient {
    protected final Map<String, T> map = new LinkedHashMap<>();
    protected final List<Class<T>> classes = new ArrayList<>();
    private final Class<T> mainType;

    public Manager() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public void preMinecraftLaunch() {
        // The main type will be null if this is not a manager that's to be handled this way
        if(mainType != null) {
            // Use reflection to discover and instantiate all feature subclasses in the "com.daniel.datsuzei" package
            final Reflections reflections = new Reflections("com.daniel.datsuzei");
            reflections.getSubTypesOf(mainType).forEach(featureClass -> {
                // Cast the class to the manager's type
                final Class<T> castClass = (Class<T>) featureClass;
                // Check if the class has an empty usable constructor
                if(ReflectionUtil.hasParameterlessConstructor(castClass)) {
                    // Add the class to a list, to be instantiated after Minecraft has launcher
                    classes.add(castClass);
                }
            });
            // Log the amount of found classes
            DatsuzeiClient.getSingleton().getLogger().info(STR."Loaded \{classes.size()} subtypes of \{mainType.getSimpleName()}");
        }
    }

    public void postMinecraftLaunch() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for(Class<T> clazz : classes) {
            try {
                T feature = clazz.getDeclaredConstructor().newInstance();
                map.put(feature.getName().toLowerCase(), feature);
            } catch (IllegalAccessException illegalAccessException) {
                DatsuzeiClient.getSingleton().getLogger().error(STR."Couldn't access class \{clazz.getName()}", illegalAccessException);
                if(DatsuzeiClient.getSingleton().isDeveloper()) {
                    DatsuzeiClient.getSingleton().getLogger().error("You forgot to make something public again, you buffoon.");
                    System.exit(0);
                }
            }

        }
    }

    public final T getByName(String name) {
        return map.get(name.toLowerCase());
    }

    public final Collection<T> getFeatures() {
        return map.values();
    }

    protected void add(String name, T type) {
        this.map.put(name, type);
    }

}
