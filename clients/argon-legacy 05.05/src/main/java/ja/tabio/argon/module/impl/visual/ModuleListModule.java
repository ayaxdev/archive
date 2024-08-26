package ja.tabio.argon.module.impl.visual;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.Render2DEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.annotation.VisualData;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.enums.VisualCategory;
import ja.tabio.argon.utils.render.DrawUtil;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@ModuleData(name = "ModuleList", category = ModuleCategory.VISUAL)
@VisualData(visualCategory = VisualCategory.HUD)
public class ModuleListModule extends Module {

    @EventHandler
    public final void on2D(Render2DEvent render2DEvent) {
        final List<Module> modules = new LinkedList<>(getModuleManager().moduleMap.values());
        modules.removeIf(module -> !module.isEnabled());

        final Comparator<Module> moduleComparator = Comparator.comparing(mod -> mc.fontRendererObj.getStringWidth(getModuleName(mod)));
        modules.sort(moduleComparator.reversed());

        float moduleY = 9;

        for (int i = 0; i < modules.size(); i++) {
            final Module module = modules.get(i);
            final String moduleName = getModuleName(module);

            final float textLength = mc.fontRendererObj.getStringWidth(moduleName);
            final float moduleWidth = 6 + textLength,
                    moduleHeight = 5.5f + mc.fontRendererObj.FONT_HEIGHT,
                    moduleX = render2DEvent.scaledResolution.getScaledWidth() - moduleWidth - 9;

            // Top border
            if (i == 0) {
                DrawUtil.drawRectRelative(moduleX, moduleY, moduleWidth, 1, Color.black.getRGB());

                moduleY += 1;

                DrawUtil.drawRectRelative(moduleX, moduleY, moduleWidth, 1, new Color(51, 153, 255).getRGB());

                moduleY += 1;
            }

            // Left & right border
            float borderExtension = i == 0 ? 2 : 0;

            DrawUtil.drawRectRelative(moduleX, moduleY - borderExtension, 1, moduleHeight + borderExtension, Color.black.getRGB());
            DrawUtil.drawRectRelative(moduleX + moduleWidth - 1, moduleY - borderExtension, 1, moduleHeight + borderExtension, Color.black.getRGB());

            // Bottom border
            if (i == modules.size() - 1) {
                DrawUtil.drawRectRelative(moduleX, moduleY + moduleHeight, moduleWidth, 1, Color.black.getRGB());
            } else {
                final Module nextModule = modules.get(i + 1);
                final float nextModuleWidth = 6 + mc.fontRendererObj.getStringWidth(getModuleName(nextModule));

                DrawUtil.drawRectRelative(moduleX, moduleY + moduleHeight, moduleWidth - nextModuleWidth, 1, Color.black.getRGB());
            }

            // Background
            DrawUtil.drawRectRelative(moduleX + 1, moduleY, moduleWidth - 2, moduleHeight, new Color(0, 0, 0, 140).getRGB());

            // Module name
            mc.fontRendererObj.drawStringOutlined(moduleName, moduleX + moduleWidth / 2 - mc.fontRendererObj.getStringWidth(moduleName) / 2f,
                    moduleY + moduleHeight / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);

            moduleY += moduleHeight;
        }
    }

    private String getModuleName(final Module module) {
        return module.displayName;
    }

}
