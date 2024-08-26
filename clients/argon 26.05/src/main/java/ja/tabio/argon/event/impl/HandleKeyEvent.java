package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import org.lwjgl.glfw.GLFW;

public class HandleKeyEvent extends Event {

    public int key, modifiers;
    public KeyAction action;

    public HandleKeyEvent(int key, int modifiers, KeyAction action) {
        this.key = key;
        this.modifiers = modifiers;
        this.action = action;
    }

    public enum KeyAction {
        PRESS,
        REPEAT,
        RELEASE;

        public static KeyAction get(int action) {
            if (action == GLFW.GLFW_PRESS) return PRESS;
            else if (action == GLFW.GLFW_RELEASE) return RELEASE;
            else return REPEAT;
        }
    }
}
