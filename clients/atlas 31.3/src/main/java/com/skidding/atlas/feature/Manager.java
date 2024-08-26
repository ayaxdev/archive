package com.skidding.atlas.feature;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.reflection.ReflectionUtil;
import lombok.Getter;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

@Getter
public class Manager<T> implements IMinecraft {
    protected final Map<String, T> map = new LinkedHashMap<>();
    protected final List<Class<T>> foundClasses = new ArrayList<>();

    public final boolean reflectPackages, reflectFields;

    public final Class<T> mainType;
    public final List<Object> objects;

    private int fromFieldCounter, fromPackageCounter;;

    public Manager(final boolean reflectPackages, final boolean reflectFields, Class<T> mainType, List<Object> objects) {
        this.mainType = mainType;
        this.objects = objects;
        this.reflectPackages = reflectPackages;
        this.reflectFields = reflectFields;
    }

    public Manager(Class<T> mainType, List<Object> objects) {
        this(false, true, mainType, objects);
    }

    public Manager(Class<T> mainType) {
        this(true, false, mainType, null);
    }

    public Manager() {
        this(false, false, null, null);
    }

    @SuppressWarnings("unchecked")
    public void preMinecraftLaunch() {
        // Register the manager to event bus, this is here instead of the constructor to prevent an infinite recursion caused by calling getInstance()
        AtlasClient.getInstance().eventPubSub.subscribe(this);
        // The main type will be null if this is not a manager that's to be handled this way
        if(reflectPackages && mainType != null) {
            // Use reflection to discover and instantiate all feature subclasses in the reflection path
            final Reflections reflections = new Reflections(mainType.getPackage().getName());
            reflections.getSubTypesOf(mainType).forEach(featureClass -> {
                // Cast the class to the manager's type
                final Class<T> castClass = (Class<T>) featureClass;
                // Check if the class has an empty usable constructor
                if(ReflectionUtil.hasParameterlessConstructor(castClass)) {
                    // Add the class to a list, to be instantiated after Minecraft has launcher
                    foundClasses.add(castClass);
                }
            });
            // Log the number of found classes
            AtlasClient.getInstance().logger.info(STR."Loaded \{foundClasses.size()} subtypes of \{mainType.getSimpleName()}");
        }

        // Reflect the fields in the inputted objects
        reflectFields();
    }

    public void postMinecraftLaunch() throws NoSuchMethodException, InvocationTargetException, InstantiationException {
        // Instantiate all found classes
        for(Class<T> clazz : foundClasses) {
            try {
                T feature = clazz.getDeclaredConstructor().newInstance();
                add(feature instanceof Feature featureCast ? featureCast.getName() : feature.getClass().getSimpleName(), feature);

                fromPackageCounter++;
            } catch (IllegalAccessException illegalAccessException) {
                AtlasClient.getInstance().logger.error(STR."Couldn't access class \{clazz.getName()}", illegalAccessException);
            }
        }

        // Reflect the fields in the inputted objects, in case the user added some in post mc launch
        reflectFields();

        // Log the number of found features and which of them are from objects and which are from classes
        AtlasClient.getInstance().logger.info(STR."Loaded \{fromFieldCounter} features from fields and \{fromPackageCounter} features from classes, \{map.size()} overall with manually added objects.");
    }

    private void reflectFields() {
        // The objects will be null if this is not a manager that's to be handled this way
        if(reflectFields && objects != null && mainType != null) {
            for(Object o : objects) {
                final Class<?> clazz = o.getClass();

                for (Field field : clazz.getDeclaredFields()) {
                    if(!Modifier.isPublic(field.getModifiers()))
                        continue;

                    try {
                        final Object declared = field.get(o);

                        if (mainType.isInstance(declared)) {
                            add(STR."\{o instanceof Feature parentFeature ? parentFeature.getName() : declared.getClass().getSimpleName()}\{declared instanceof Feature feature ? feature.getName() : field.getName()}:", (T) declared);

                            fromFieldCounter++;
                        }
                    } catch (IllegalAccessException illegalAccessException) {
                        AtlasClient.getInstance().logger.error(STR."Couldn't access field \{field.getName()}", illegalAccessException);
                    }
                }
            }

            objects.clear();
        }

    }

    public final T getByName(String name) {
        return getByName(name, false);
    }

    public final T getByName(String name, boolean strict) {
        if(strict)
            return map.get(name);
        else {
            final Map.Entry<String, T> foundEntry = map.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(name)).findFirst().orElse(null);

            return foundEntry == null ? null : foundEntry.getValue();
        }
    }

    @SuppressWarnings("unchecked")
    public final <T1 extends T> T1 getByClass(Class<T1> clazz) {
        return (T1) map.values().stream().filter(feature -> feature.getClass() == clazz).findFirst().orElse(null);
    }

    public final ArrayList<T> getFeatures() {
        return new ArrayList<>(map.values());
    }

    protected void add(String name, T type) {
        this.map.put(name, type);
    }

}