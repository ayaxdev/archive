package ja.tabio.argon.component.render;

import org.lwjgl.glfw.GLFW;

public class RenderManager {

    private double prevGLTime = Double.NaN;
    private double fps;

    public void updateFrame() {
        if (Double.isNaN(prevGLTime)) {
            prevGLTime = GLFW.glfwGetTime();
            return;
        }
        double time = GLFW.glfwGetTime();
        double delta = time - prevGLTime;
        fps = 1.0 / delta;
        prevGLTime = time;
    }

    public double getFps() {
        return fps;
    }

}
