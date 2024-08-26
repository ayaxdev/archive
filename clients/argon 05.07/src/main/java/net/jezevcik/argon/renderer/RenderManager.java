package net.jezevcik.argon.renderer;

import org.lwjgl.glfw.GLFW;

public class RenderManager {

    private static double prevGLTime = Double.NaN;
    private static double fps;

    public static void drawFrame() {
        if (Double.isNaN(prevGLTime)) {
            prevGLTime = GLFW.glfwGetTime();
            return;
        }
        double time = GLFW.glfwGetTime();
        double delta = time - prevGLTime;
        fps = 1.0 / delta;
        prevGLTime = time;
    }

    public static double getFps() {
        return fps;
    }

}
