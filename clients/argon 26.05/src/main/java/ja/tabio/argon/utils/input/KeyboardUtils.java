package ja.tabio.argon.utils.input;

import ja.tabio.argon.interfaces.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyboardUtils implements Minecraft {

    public static boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

}
