package com.atani.nextgen.feature;

import com.atani.nextgen.AtaniClient;
import com.atani.nextgen.event.Event;
import com.atani.nextgen.util.minecraft.MinecraftClient;
import com.atani.nextgen.util.reflection.ReflectionUtil;
import lombok.Getter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Manager<T> implements MinecraftClient {
    protected final Map<String, T> map = new LinkedHashMap<>();
    protected final List<Class<T>> classes = new ArrayList<>();

    public final Class<T> mainType;

    public Manager(Class<T> mainType) {
        this.mainType = mainType;
    }

    public Manager() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public void preMinecraftLaunch() {
        // Register the manager to event bus, this is here instead of the constructor to prevent an infinite recursion caused by calling getInstance()
        AtaniClient.getInstance().eventPubSub.subscribe(this);
        // The main type will be null if this is not a manager that's to be handled this way
        if(mainType != null) {
            // Use reflection to discover and instantiate all feature subclasses in the reflection path
            final Reflections reflections = new Reflections(mainType.getPackage().getName());
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
            AtaniClient.getInstance().logger.info(STR."Loaded \{classes.size()} subtypes of \{mainType.getSimpleName()}");
        }
    }

    public void postMinecraftLaunch() throws NoSuchMethodException, InvocationTargetException, InstantiationException {
        for(Class<T> clazz : classes) {
            try {
                T feature = clazz.getDeclaredConstructor().newInstance();
                add(feature instanceof Feature featureCast ? featureCast.getName() : feature.getClass().getSimpleName(), feature);
            } catch (IllegalAccessException illegalAccessException) {
                AtaniClient.getInstance().logger.error(STR."Couldn't access class \{clazz.getName()}", illegalAccessException);
            }
        }
    }

    public final T getByName(String name) {
        return map.get(name.toLowerCase());
    }

    public final ArrayList<T> getFeatures() {
        return new ArrayList<>(map.values());
    }

    protected void add(String name, T type) {
        this.map.put(name.toLowerCase(), type);
    }

}