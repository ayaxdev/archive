package ja.tabio.argon.component.imgui.themes;

import imgui.ImColor;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import ja.tabio.argon.Argon;
import ja.tabio.argon.utils.jvm.StringUtils;
import net.minecraft.util.Pair;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ThemeTOMLParser {

    private static final Map<String, Consumer<Float[]>> INSTRUCTION_MAP = new HashMap<>();
    private static final Map<String, Consumer<Integer>> COLOR_INSTRUCTION_MAP = new HashMap<>();
    public static final List<Theme> THEMES = new ArrayList<>();

    public static final File IMGUI_FOLDER = new File(Argon.getInstance().directory, "imthemes");

    public static void setup(ImGuiStyle style) {
        INSTRUCTION_MAP.clear();
        COLOR_INSTRUCTION_MAP.clear();
        THEMES.clear();

        INSTRUCTION_MAP.put("alpha", args -> style.setAlpha(args[0]));
        INSTRUCTION_MAP.put("disabledAlpha", args -> style.setDisabledAlpha(args[0]));
        INSTRUCTION_MAP.put("windowPadding", args -> style.setWindowPadding(args[0], args[1]));
        INSTRUCTION_MAP.put("windowRounding", args -> style.setWindowRounding(args[0]));
        INSTRUCTION_MAP.put("windowBorderSize", args -> style.setWindowBorderSize(args[0]));
        INSTRUCTION_MAP.put("windowMinSize", args -> style.setWindowMinSize(args[0], args[1]));
        INSTRUCTION_MAP.put("windowTitleAlign", args -> style.setWindowTitleAlign(args[0], args[1]));
        INSTRUCTION_MAP.put("windowMenuButtonPosition", args -> style.setWindowMenuButtonPosition(args[0].intValue()));
        INSTRUCTION_MAP.put("childRounding", args -> style.setChildRounding(args[0]));
        INSTRUCTION_MAP.put("childBorderSize", args -> style.setChildBorderSize(args[0]));
        INSTRUCTION_MAP.put("popupRounding", args -> style.setPopupRounding(args[0]));
        INSTRUCTION_MAP.put("popupBorderSize", args -> style.setPopupBorderSize(args[0]));
        INSTRUCTION_MAP.put("framePadding", args -> style.setFramePadding(args[0], args[1]));
        INSTRUCTION_MAP.put("frameRounding", args -> style.setFrameRounding(args[0]));
        INSTRUCTION_MAP.put("frameBorderSize", args -> style.setFrameBorderSize(args[0]));
        INSTRUCTION_MAP.put("itemSpacing", args -> style.setItemSpacing(args[0], args[1]));
        INSTRUCTION_MAP.put("itemInnerSpacing", args -> style.setItemInnerSpacing(args[0], args[1]));
        INSTRUCTION_MAP.put("cellPadding", args -> style.setCellPadding(args[0], args[1]));
        INSTRUCTION_MAP.put("indentSpacing", args -> style.setIndentSpacing(args[0]));
        INSTRUCTION_MAP.put("columnsMinSpacing", args -> style.setColumnsMinSpacing(args[0]));
        INSTRUCTION_MAP.put("scrollbarSize", args -> style.setScrollbarSize(args[0]));
        INSTRUCTION_MAP.put("scrollbarRounding", args -> style.setScrollbarRounding(args[0]));
        INSTRUCTION_MAP.put("grabMinSize", args -> style.setGrabMinSize(args[0]));
        INSTRUCTION_MAP.put("grabRounding", args -> style.setGrabRounding(args[0]));
        INSTRUCTION_MAP.put("tabRounding", args -> style.setTabRounding(args[0]));
        INSTRUCTION_MAP.put("tabBorderSize", args -> style.setTabBorderSize(args[0]));
        INSTRUCTION_MAP.put("tabMinWidthForCloseButton", args -> style.setTabMinWidthForCloseButton(args[0]));
        INSTRUCTION_MAP.put("colorButtonPosition", args -> style.setColorButtonPosition(args[0].intValue()));
        INSTRUCTION_MAP.put("buttonTextAlign", args -> style.setButtonTextAlign(args[0], args[1]));
        INSTRUCTION_MAP.put("selectableTextAlign", args -> style.setSelectableTextAlign(args[0], args[1]));

        COLOR_INSTRUCTION_MAP.put("Text", color -> style.setColor(ImGuiCol.Text, color));
        COLOR_INSTRUCTION_MAP.put("TextDisabled", color -> style.setColor(ImGuiCol.TextDisabled, color));
        COLOR_INSTRUCTION_MAP.put("WindowBg", color -> style.setColor(ImGuiCol.WindowBg, color));
        COLOR_INSTRUCTION_MAP.put("ChildBg", color -> style.setColor(ImGuiCol.ChildBg, color));
        COLOR_INSTRUCTION_MAP.put("PopupBg", color -> style.setColor(ImGuiCol.PopupBg, color));
        COLOR_INSTRUCTION_MAP.put("Border", color -> style.setColor(ImGuiCol.Border, color));
        COLOR_INSTRUCTION_MAP.put("BorderShadow", color -> style.setColor(ImGuiCol.BorderShadow, color));
        COLOR_INSTRUCTION_MAP.put("FrameBg", color -> style.setColor(ImGuiCol.FrameBg, color));
        COLOR_INSTRUCTION_MAP.put("FrameBgHovered", color -> style.setColor(ImGuiCol.FrameBgHovered, color));
        COLOR_INSTRUCTION_MAP.put("FrameBgActive", color -> style.setColor(ImGuiCol.FrameBgActive, color));
        COLOR_INSTRUCTION_MAP.put("TitleBg", color -> style.setColor(ImGuiCol.TitleBg, color));
        COLOR_INSTRUCTION_MAP.put("TitleBgActive", color -> style.setColor(ImGuiCol.TitleBgActive, color));
        COLOR_INSTRUCTION_MAP.put("TitleBgCollapsed", color -> style.setColor(ImGuiCol.TitleBgCollapsed, color));
        COLOR_INSTRUCTION_MAP.put("MenuBarBg", color -> style.setColor(ImGuiCol.MenuBarBg, color));
        COLOR_INSTRUCTION_MAP.put("ScrollbarBg", color -> style.setColor(ImGuiCol.ScrollbarBg, color));
        COLOR_INSTRUCTION_MAP.put("ScrollbarGrab", color -> style.setColor(ImGuiCol.ScrollbarGrab, color));
        COLOR_INSTRUCTION_MAP.put("ScrollbarGrabHovered", color -> style.setColor(ImGuiCol.ScrollbarGrabHovered, color));
        COLOR_INSTRUCTION_MAP.put("ScrollbarGrabActive", color -> style.setColor(ImGuiCol.ScrollbarGrabActive, color));
        COLOR_INSTRUCTION_MAP.put("CheckMark", color -> style.setColor(ImGuiCol.CheckMark, color));
        COLOR_INSTRUCTION_MAP.put("SliderGrab", color -> style.setColor(ImGuiCol.SliderGrab, color));
        COLOR_INSTRUCTION_MAP.put("SliderGrabActive", color -> style.setColor(ImGuiCol.SliderGrabActive, color));
        COLOR_INSTRUCTION_MAP.put("Button", color -> style.setColor(ImGuiCol.Button, color));
        COLOR_INSTRUCTION_MAP.put("ButtonHovered", color -> style.setColor(ImGuiCol.ButtonHovered, color));
        COLOR_INSTRUCTION_MAP.put("ButtonActive", color -> style.setColor(ImGuiCol.ButtonActive, color));
        COLOR_INSTRUCTION_MAP.put("Header", color -> style.setColor(ImGuiCol.Header, color));
        COLOR_INSTRUCTION_MAP.put("HeaderHovered", color -> style.setColor(ImGuiCol.HeaderHovered, color));
        COLOR_INSTRUCTION_MAP.put("HeaderActive", color -> style.setColor(ImGuiCol.HeaderActive, color));
        COLOR_INSTRUCTION_MAP.put("Separator", color -> style.setColor(ImGuiCol.Separator, color));
        COLOR_INSTRUCTION_MAP.put("SeparatorHovered", color -> style.setColor(ImGuiCol.SeparatorHovered, color));
        COLOR_INSTRUCTION_MAP.put("SeparatorActive", color -> style.setColor(ImGuiCol.SeparatorActive, color));
        COLOR_INSTRUCTION_MAP.put("ResizeGrip", color -> style.setColor(ImGuiCol.ResizeGrip, color));
        COLOR_INSTRUCTION_MAP.put("ResizeGripHovered", color -> style.setColor(ImGuiCol.ResizeGripHovered, color));
        COLOR_INSTRUCTION_MAP.put("ResizeGripActive", color -> style.setColor(ImGuiCol.ResizeGripActive, color));
        COLOR_INSTRUCTION_MAP.put("Tab", color -> style.setColor(ImGuiCol.Tab, color));
        COLOR_INSTRUCTION_MAP.put("TabHovered", color -> style.setColor(ImGuiCol.TabHovered, color));
        COLOR_INSTRUCTION_MAP.put("TabActive", color -> style.setColor(ImGuiCol.TabActive, color));
        COLOR_INSTRUCTION_MAP.put("TabUnfocused", color -> style.setColor(ImGuiCol.TabUnfocused, color));
        COLOR_INSTRUCTION_MAP.put("TabUnfocusedActive", color -> style.setColor(ImGuiCol.TabUnfocusedActive, color));
        COLOR_INSTRUCTION_MAP.put("DockingPreview", color -> style.setColor(ImGuiCol.DockingPreview, color));
        COLOR_INSTRUCTION_MAP.put("DockingEmptyBg", color -> style.setColor(ImGuiCol.DockingEmptyBg, color));
        COLOR_INSTRUCTION_MAP.put("PlotLines", color -> style.setColor(ImGuiCol.PlotLines, color));
        COLOR_INSTRUCTION_MAP.put("PlotLinesHovered", color -> style.setColor(ImGuiCol.PlotLinesHovered, color));
        COLOR_INSTRUCTION_MAP.put("PlotHistogram", color -> style.setColor(ImGuiCol.PlotHistogram, color));
        COLOR_INSTRUCTION_MAP.put("PlotHistogramHovered", color -> style.setColor(ImGuiCol.PlotHistogramHovered, color));
        COLOR_INSTRUCTION_MAP.put("TableHeaderBg", color -> style.setColor(ImGuiCol.TableHeaderBg, color));
        COLOR_INSTRUCTION_MAP.put("TableBorderStrong", color -> style.setColor(ImGuiCol.TableBorderStrong, color));
        COLOR_INSTRUCTION_MAP.put("TableBorderLight", color -> style.setColor(ImGuiCol.TableBorderLight, color));
        COLOR_INSTRUCTION_MAP.put("TableRowBg", color -> style.setColor(ImGuiCol.TableRowBg, color));
        COLOR_INSTRUCTION_MAP.put("TableRowBgAlt", color -> style.setColor(ImGuiCol.TableRowBgAlt, color));
        COLOR_INSTRUCTION_MAP.put("TextSelectedBg", color -> style.setColor(ImGuiCol.TextSelectedBg, color));
        COLOR_INSTRUCTION_MAP.put("DragDropTarget", color -> style.setColor(ImGuiCol.DragDropTarget, color));
        COLOR_INSTRUCTION_MAP.put("NavHighlight", color -> style.setColor(ImGuiCol.NavHighlight, color));
        COLOR_INSTRUCTION_MAP.put("NavWindowingHighlight", color -> style.setColor(ImGuiCol.NavWindowingHighlight, color));
        COLOR_INSTRUCTION_MAP.put("NavWindowingDimBg", color -> style.setColor(ImGuiCol.NavWindowingDimBg, color));
        COLOR_INSTRUCTION_MAP.put("ModalWindowDimBg", color -> style.setColor(ImGuiCol.ModalWindowDimBg, color));
        COLOR_INSTRUCTION_MAP.put("COUNT", color -> style.setColor(ImGuiCol.COUNT, color));

        if (!IMGUI_FOLDER.exists()) {
            if (!IMGUI_FOLDER.mkdirs()) {
                Argon.getInstance().logger.error("Failed to create imgui folder");
                return;
            }
        }

        if(!checkForMainFile())
            Argon.getInstance().logger.error("Couldn't access the main themes file");

        if(!findThemes())
            Argon.getInstance().logger.error("There was an error finding themes");
    }

    public static boolean findThemes() {
        for (File file : IMGUI_FOLDER.listFiles()) {
            if (file.getName().toLowerCase().contains(".toml")) {
                try {
                    load(FileUtils.readFileToString(file, "UTF-8"));
                } catch (IOException e) {
                    Argon.getInstance().logger.error("There was an error reading the {} theme file", file.getAbsolutePath(), e);
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean checkForMainFile() {
        final File mainFile = new File(IMGUI_FOLDER, "themes.toml");

        if (!mainFile.exists()) {
            try {
                if(!mainFile.createNewFile()) {
                    Argon.getInstance().logger.error("Failed to create the main themes.toml file");
                    return false;
                }
            } catch (IOException ioException) {
                Argon.getInstance().logger.error("Failed to create the main themes file", ioException);
            }

            try (final FileWriter fileWriter = new FileWriter(mainFile)) {
                final URL u = new URI("https://raw.githubusercontent.com/Patitotective/ImThemes/main/themes.toml").toURL();
                final URLConnection conn = u.openConnection();
                final BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));
                final StringBuilder builder = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("#"))
                        continue;

                    builder.append(inputLine);
                    builder.append("\n");
                }

                in.close();
                fileWriter.write(builder.toString());
            } catch (IOException e) {
                Argon.getInstance().logger.error("Failed to write to the main themes file", e);
            } catch (URISyntaxException e) {
                Argon.getInstance().logger.error("Failed to download latest themes", e);
            }
        }

        return true;
    }

    public static void load(String input) {
        final StringBuilder clearedOutput = new StringBuilder();

        for(String line : input.split("\n")) {
            if(line.startsWith("#"))
                continue;

            line = line.replaceAll("^ {2,}", "");
            clearedOutput.append(line).append("\n");
        }

        final String clearedOutputString = clearedOutput.toString();
        final String[] themes = clearedOutputString.contains("[[themes]]") ? clearedOutputString.split("\\[\\[themes\\]\\]") : new String[] {clearedOutputString};

        for(String theme : themes) {
            String name = "";

            final StringBuilder instructions = new StringBuilder();
            boolean instructing = false;

            for(String line : theme.split("\n")) {
                if(line.contains("name = ")) {
                    name = StringUtils.extractStringBetween(line, "\"", "\"");
                }

                if(line.contains("[themes.style]")) {
                    instructing = true;
                }

                if(instructing) {
                    instructions.append(line);
                    instructions.append("\n");
                }
            }

            ThemeTOMLParser.THEMES.add(parseTheme(name, instructions.toString()));
        }
    }

    public static Theme parseTheme(String themeName, String theme) {
        final Map<Consumer<Float[]>, Float[]> instructions = new HashMap<>();
        final Map<Consumer<Integer>, Integer> colorInstructions = new HashMap<>();

        boolean processingColors = false;

        for(String line : theme.split("\n")) {
            if(line.startsWith("#"))
                continue;

            if(line.isEmpty())
                continue;

            if(line.contains("style.colors")) {
                processingColors = true;
                continue;
            }

            if(line.contains("[") || line.contains("]"))
                continue;

            try {
                final String[] instructionSplit = line.split(" = ");
                String name = instructionSplit[0],
                        value = instructionSplit[1];

                if (processingColors) {
                    String[] splitVector = value.substring(6, value.length() - 2).split(", ");
                    if(COLOR_INSTRUCTION_MAP.containsKey(name))
                        colorInstructions.put(COLOR_INSTRUCTION_MAP.get(name), ImColor.floatToColor(Integer.parseInt(splitVector[0]) / 255f, Integer.parseInt(splitVector[1]) / 255f, Integer.parseInt(splitVector[2]) / 255f, (float) Double.parseDouble(splitVector[3])));
                } else {
                    if (value.startsWith("[")) {
                        value = value.substring(1, value.length() - 1);
                        String[] valueSplit = value.split(", ");

                        instructions.put(INSTRUCTION_MAP.get(name), new Float[]{Float.parseFloat(valueSplit[0]), Float.parseFloat(valueSplit[1])});
                    } else {
                        if(!value.contains("\""))
                            instructions.put(INSTRUCTION_MAP.get(name), new Float[]{Float.parseFloat(value)});
                    }
                }
            } catch (Exception e) {
                Argon.getInstance().logger.error("There was an error parsing a line in a theme", e);
            }
        }

        return new Theme(themeName, instructions, colorInstructions);
    }
    
    public static void useTheme(Theme theme) {
        for (Map.Entry<Consumer<Float[]>, Float[]> instruction : theme.instructions.getLeft().entrySet()) {
            instruction.getKey().accept(instruction.getValue());
        }
        for (Map.Entry<Consumer<Integer>, Integer> colorInstruction : theme.instructions.getRight().entrySet()) {
            colorInstruction.getKey().accept(colorInstruction.getValue());
        }
    }

    public static class Theme {
        public final Pair<Map<Consumer<Float[]>, Float[]>, Map<Consumer<Integer>, Integer>> instructions;
        public final String name;

        public Theme(String name, Map<Consumer<Float[]>, Float[]> left, Map<Consumer<Integer>, Integer> right) {
            this.instructions = new Pair<>(left, right);
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}