package ja.tabio.argon.setting.impl;

import ja.tabio.argon.setting.Setting;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class KeySetting extends Setting<Integer> {

    private int value;

    public KeySetting(String name, String displayName, int value) {
        super(name, displayName);

        if (GLFW.glfwGetKeyName(value, 0) != null)
            this.value = value;
        else
            throw new IllegalArgumentException("Invalid key value!");
    }

    public KeySetting(String name, int value) {
        this(name, name, value);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        if (Objects.equals(this.value, value))
            return;

        final int oldValue = this.value;
        this.changeListener.onChange(true, oldValue, value);

        if (GLFW.glfwGetKeyName(value, 0) != null)
            this.value = value;
        else
            throw new IllegalArgumentException("Invalid key value!");

        this.changeListener.onChange(false, oldValue, value);
    }
}
