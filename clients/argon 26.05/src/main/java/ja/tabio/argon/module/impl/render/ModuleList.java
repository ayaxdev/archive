package ja.tabio.argon.module.impl.render;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.component.font.MinecraftFontRendererAdapter;
import ja.tabio.argon.component.render.Renderer2D;
import ja.tabio.argon.event.impl.Render2DEvent;
import ja.tabio.argon.font.style.StyledText;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@RegisterModule
public class ModuleList extends Module {

    public ModuleList() {
        super(ModuleParams.builder()
                .name("ModuleList")
                .category(ModuleCategory.RENDER)
                .enabled(true)
                .build());
    }

    private final StyledText argonStyle = StyledText.builder().type(StyledText.Type.ARGON).build();

    @EventHandler
    public final void on2D(Render2DEvent render2DEvent) {
        final Renderer2D renderer2D = new Renderer2D(render2DEvent.drawContext);

        final List<Module> modules = new LinkedList<>(Argon.getInstance().moduleManager.moduleMap.values());
        modules.removeIf(module -> !module.isEnabled());

        final Comparator<Module> moduleComparator = Comparator.comparing(mod -> mc.textRenderer.getWidth(getModuleName(mod)));
        modules.sort(moduleComparator.reversed());

        float moduleY = 9;

        for (int i = 0; i < modules.size(); i++) {
            final Module module = modules.get(i);
            final String moduleName = getModuleName(module);

            final float textLength = mc.textRenderer.getWidth(moduleName);
            final float moduleWidth = 6 + textLength,
                    moduleHeight = 5f + mc.textRenderer.fontHeight,
                    moduleX = render2DEvent.screenWidth - moduleWidth - 9;

            // Top border
            if (i == 0) {
                renderer2D.drawRect(moduleX, moduleY, moduleWidth, 1, Color.black.getRGB());

                moduleY += 1;

                renderer2D.drawRect(moduleX, moduleY, moduleWidth, 1, new Color(51, 153, 255).getRGB());

                moduleY += 1;
            }

            // Left & right border
            float borderExtension = i == 0 ? 2 : 0;

            renderer2D.drawRect(moduleX, moduleY - borderExtension, 1, moduleHeight + borderExtension, Color.black.getRGB());
            renderer2D.drawRect(moduleX + moduleWidth - 1, moduleY - borderExtension, 1, moduleHeight + borderExtension, Color.black.getRGB());

            // Bottom border
            if (i == modules.size() - 1) {
                renderer2D.drawRect(moduleX, moduleY + moduleHeight, moduleWidth, 1, Color.black.getRGB());
            } else {
                final Module nextModule = modules.get(i + 1);
                final float nextModuleWidth = 6 + mc.textRenderer.getWidth(getModuleName(nextModule));

                renderer2D.drawRect(moduleX, moduleY + moduleHeight, moduleWidth - nextModuleWidth, 1, Color.black.getRGB());
            }

            // Background
            renderer2D.drawRect(moduleX + 1, moduleY, moduleWidth - 2, moduleHeight, new Color(0, 0, 0, 140).getRGB());

            // Module name
            argonStyle.prepare(renderer2D, MinecraftFontRendererAdapter.INSTANCE);
            argonStyle.drawString(moduleName,  moduleX + moduleWidth / 2 - mc.textRenderer.getWidth(moduleName) / 2f,
                    moduleY + moduleHeight / 2f - mc.textRenderer.fontHeight / 2f + 1f, -1);

            moduleY += moduleHeight;
        }
    }

    private String getModuleName(final Module module) {
        return module.getDisplayName();
    }

}
