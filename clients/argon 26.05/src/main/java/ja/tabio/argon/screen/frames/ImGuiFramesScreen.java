package ja.tabio.argon.screen.frames;

import imgui.ImGui;
import imgui.ImGuiIO;
import ja.tabio.argon.Argon;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.screen.frames.impl.ImGuiFrame;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ImGuiFramesScreen extends Screen {

    private final List<ImGuiFrame> frames = new ArrayList<>();
    private boolean demo = false;

    public ImGuiFramesScreen() {
        super(Text.literal("ImGui"));

        float x = 5;
        for (ModuleCategory category : ModuleCategory.values()) {
            frames.add(new ImGuiFrame(category, x));
            x += 200 + 5;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Argon.getInstance().imGui.draw(this::drawImGui);
    }

    private void drawImGui(ImGuiIO imGuiIO) {
        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Frames")) {
            if (ImGui.menuItem("Demo",  "", demo))
                demo = !demo;

            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();

        if (demo)
            ImGui.showDemoWindow();

        for (ImGuiFrame frame : frames) {
            frame.render();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            for (ImGuiFrame frame : frames) {
                if (frame.expanded != null) {
                    frame.closeOnNextFrame = true;

                    return true;
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
