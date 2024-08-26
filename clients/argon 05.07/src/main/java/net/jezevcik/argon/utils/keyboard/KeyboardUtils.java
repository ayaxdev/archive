package net.jezevcik.argon.utils.keyboard;

import net.jezevcik.argon.system.minecraft.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class KeyboardUtils implements Minecraft {

    public static final Map<String, Integer> KEY_MAP = new HashMap<>();

    static {
        for (Field field : GLFW.class.getFields()) {
            if (field.getName().startsWith("GLFW_KEY_")) {
                try {
                    KEY_MAP.put(field.getName().substring(9), field.getInt(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static boolean isKeyDown(final int key) {
        return GLFW.glfwGetKey(client.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    public static int getKeyIndex(final String key) throws IllegalArgumentException {
        final String upper = key.toUpperCase();
        if (!KEY_MAP.containsKey(upper))
            throw new IllegalArgumentException("Key not found: " + key);
        else
            return KEY_MAP.get(upper);
    }


}
