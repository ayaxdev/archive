package ja.tabio.argon.component.imgui.themes;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiDir;

public class ImGuiThemes {

    public static void setDefaultProperties() {
        final ImGuiStyle style = ImGui.getStyle();

        style.setAlpha(1.0f);
        style.setDisabledAlpha(0.60f);
        style.setWindowPadding(8,8);
        style.setWindowRounding(0.0f);
        style.setWindowBorderSize(1.0f);
        style.setWindowMinSize(32,32);
        style.setWindowTitleAlign(0.0f,0.5f);
        style.setWindowMenuButtonPosition(ImGuiDir.Left);
        style.setChildRounding(0.0f);
        style.setChildBorderSize(1.0f);
        style.setPopupRounding(0.0f);
        style.setPopupBorderSize(1.0f);
        style.setFramePadding(4,3);
        style.setFrameRounding(0.0f);
        style.setFrameBorderSize(0.0f);
        style.setItemSpacing(8,4);
        style.setItemInnerSpacing(4,4);
        style.setCellPadding(4,2);
        style.setTouchExtraPadding(0,0);
        style.setIndentSpacing(21.0f);
        style.setColumnsMinSpacing(6.0f);
        style.setScrollbarSize(14.0f);
        style.setScrollbarRounding(9.0f);
        style.setGrabMinSize(12.0f);
        style.setGrabRounding(0.0f);
        style.setLogSliderDeadzone(4.0f);
        style.setTabRounding(4.0f);
        style.setTabBorderSize(0.0f);
        style.setTabMinWidthForCloseButton(0.0f);
        style.setColorButtonPosition(ImGuiDir.Right);
        style.setButtonTextAlign(0.5f,0.5f);
        style.setSelectableTextAlign(0.0f,0.0f);
        style.setDisplayWindowPadding(19,19);
        style.setDisplaySafeAreaPadding(3,3);
        style.setMouseCursorScale(1.0f);
        style.setAntiAliasedLines(true);
        style.setAntiAliasedLinesUseTex(true);
        style.setAntiAliasedFill(true);
        style.setCurveTessellationTol(1.25f);
        style.setCircleTessellationMaxError(0.30f);
    }

}
