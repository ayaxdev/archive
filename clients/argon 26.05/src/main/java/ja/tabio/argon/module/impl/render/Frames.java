package ja.tabio.argon.module.impl.render;

import imgui.ImGui;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.imgui.themes.ImGuiThemes;
import ja.tabio.argon.component.imgui.themes.ThemeTOMLParser;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.screen.frames.ImGuiFramesScreen;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@RegisterModule
public class Frames extends Module {

    public final ModeSetting style = new ModeSetting("Style", "BasicCustom", getModes())
            .change((pre, oldValue, newValue) -> {
                if (!pre)
                    refresh();
            });
    public final ModeSetting color = new ModeSetting("Color", "Classic", new String[] {"Classic", "Dark", "Light"})
            .visibility(style, "BasicCustom")
            .change((pre, oldValue, newValue) -> {
                if (!pre)
                    refresh();
            });
    public final NumberSetting frameRounding = new NumberSetting("FrameRounding", 7, 0, 16, 0)
            .visibility(style, "BasicCustom")
            .change((pre, oldValue, newValue) -> {
                if (!pre)
                    refresh();
            });
    public final NumberSetting windowRounding = new NumberSetting("WindowRounding", 7, 0, 16, 0)
            .visibility(style, "BasicCustom")
            .change((pre, oldValue, newValue) -> {
                if (!pre)
                    refresh();
            });

    private ImGuiFramesScreen screen;
    private boolean initialized;

    public Frames() {
        super(ModuleParams.builder()
                .name("Frames")
                .category(ModuleCategory.RENDER)
                .key(GLFW.GLFW_KEY_RIGHT_SHIFT)
                .build());
    }

    @Override
    public boolean onEnable() {
        if(!initialized) {
            refresh();

            initialized = true;
        }

        if (screen == null)
            screen = new ImGuiFramesScreen();

        mc.setScreen(screen);

        return false;
    }

    private Object[] getModes() {
        final ArrayList<String> modes = new ArrayList<>();

        modes.add("BasicCustom");

        for (Object o : ThemeTOMLParser.THEMES.toArray()) {
            final String mode = o.toString();

            if (mode.isBlank() || mode.isEmpty())
                continue;

            modes.add(mode.replace(" ", ""));
        }

        return modes.toArray();
    }

    private void refresh() {
        if(!Argon.getInstance().loaded)
            return;

        // Resets the theme before changing one
        ImGuiThemes.setDefaultProperties();
        ImGui.styleColorsDark();

        if(style.getValue().equalsIgnoreCase("BasicCustom")) {
            switch (color.getValue()) {
                case "Light" -> ImGui.styleColorsLight();
                case "Classic" -> ImGui.styleColorsClassic();
                default -> ImGui.styleColorsDark();
            }
            ImGuiThemes.setDefaultProperties();

            ImGui.getStyle().setFrameRounding(frameRounding.getValue());
            ImGui.getStyle().setWindowRounding(windowRounding.getValue());
        } else {
            ThemeTOMLParser.Theme curTheme = null;
            for(ThemeTOMLParser.Theme theme : ThemeTOMLParser.THEMES) {
                if(theme.toString().replace(" ", "").equalsIgnoreCase(style.getValue())) {
                    curTheme = theme;
                    break;
                }
            }

            if(curTheme != null) {
                ThemeTOMLParser.useTheme(curTheme);
            } else {
                ImGui.styleColorsDark();
                ImGuiThemes.setDefaultProperties();

                Argon.getInstance().logger.error("Theme of current mode not found");
            }
        }
    }


}
