package ja.tabio.argon.component.imgui;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImGuiImpl {

    private final static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    private final List<ImFont> generatedFonts = new ArrayList<>();

    public ImGuiImpl(final long handle, final String configFile, final int configFlags, final ImGuiFont[] customFonts) throws IOException {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename(String.format("%s.ini", configFile));
        data.setFontGlobalScale(1F);

        if(customFonts.length != 0) {
            final ImFontAtlas fonts = data.getFonts();
            final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();

            rangesBuilder.addRanges(data.getFonts().getGlyphRangesDefault());
            rangesBuilder.addRanges(data.getFonts().getGlyphRangesCyrillic());
            rangesBuilder.addRanges(data.getFonts().getGlyphRangesJapanese());

            final short[] glyphRanges = rangesBuilder.buildRanges();

            final ImFontConfig basicConfig = new ImFontConfig();
            basicConfig.setGlyphRanges(data.getFonts().getGlyphRangesCyrillic());

            for(ImGuiFont imGuiFont : customFonts) {
                for (int i = imGuiFont.minimumSize; i < imGuiFont.maximumSize; i++) {
                    basicConfig.setName(imGuiFont.fontName + i + "px");
                    generatedFonts.add(fonts.addFontFromMemoryTTF(IOUtils.toByteArray(Objects.requireNonNull(ImGuiImpl.class.getResourceAsStream(imGuiFont.fontPath))), i, basicConfig, glyphRanges));
                }
            }
            fonts.build();
            basicConfig.destroy();
        }

        data.setConfigFlags(configFlags);

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
    }

    public void draw(final ImGuiRenderContext runnable) {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        runnable.draw(ImGui.getIO());
        ImGui.render();

        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    public record ImGuiFont(String fontName, String fontPath, int minimumSize, int maximumSize) {}

    public interface ImGuiRenderContext { void draw(ImGuiIO imGuiIO); }

    public static class ImGuiBuilder {

        private final long handle;
        private final String configFile;
        private int configFlags = ImGuiConfigFlags.DockingEnable;
        private ImGuiFont[] imGuiFonts = new ImGuiFont[0];

        public ImGuiBuilder(long handle, String configFile) {
            try {
                this.handle = handle;
                this.configFile = Objects.requireNonNull(configFile);
            } catch (NullPointerException nullPointerException) {
                throw new IllegalArgumentException(nullPointerException);
            }
        }

        public ImGuiBuilder setConfigFlags(int configFlags) {
            this.configFlags = configFlags;
            return this;
        }

        public ImGuiBuilder setFonts(ImGuiFont... imGuiFonts) {
            this.imGuiFonts = imGuiFonts;
            return this;
        }

        public ImGuiImpl build() throws IOException {
            return new ImGuiImpl(handle, configFile, configFlags, imGuiFonts);
        }
    }

}
