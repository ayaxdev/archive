package ja.tabio.argon.module.impl.visual;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.Render2DEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.annotation.VisualData;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.enums.VisualCategory;
import ja.tabio.argon.setting.impl.ModeSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.Collection;

@ModuleData(name = "Watermark", enabled = true, category = ModuleCategory.VISUAL)
@VisualData(visualCategory = VisualCategory.HUD)
public class WatermarkModule extends Module {

    public final ModeSetting mode = new ModeSetting("Mode", "Argon", "Argon");
    public final ModeSetting argonText = new ModeSetting("ArgonText", "Cyrillic", "Cyrillic", "Latin")
            .visibility(() -> mode.getValue().equalsIgnoreCase("Argon"));

    @EventHandler
    public final void on2D(Render2DEvent render2DEvent) {
        if (mode.getValue().equalsIgnoreCase("Argon")) {
            final String argonText = this.argonText.getValue().equalsIgnoreCase("Latin") ? Argon.LATIN_NAME : Argon.CYRILLIC_NAME;
            final String name = argonText.charAt(0) + EnumChatFormatting.WHITE.toString() + argonText.substring(1);
            final String text = String.format("%s v%s | %s FPS | %s | %s", name, Argon.VERSION, Minecraft.getDebugFPS(), mc.getSession().getUsername(), mc.getCurrentServerData() == null ? "SinglePlayer" : mc.getCurrentServerData().serverIP);
            final float x = 9, y = 8f;

            Gui.drawRect(x, y, x + 1, y + 6.5f + mc.fontRendererObj.FONT_HEIGHT, Color.black.getRGB());
            Gui.drawRect(x + 5 + mc.fontRendererObj.getStringWidth(text), y, x + 5 + mc.fontRendererObj.getStringWidth(text) + 1, y + 6.5f + mc.fontRendererObj.FONT_HEIGHT, Color.black.getRGB());

            Gui.drawRect(x, y, x + 5 + mc.fontRendererObj.getStringWidth(text) + 1, y + 1, Color.black.getRGB());
            Gui.drawRect(x, y + 5.5f + mc.fontRendererObj.FONT_HEIGHT, x + 5 + mc.fontRendererObj.getStringWidth(text) + 1, y + 6.5f + mc.fontRendererObj.FONT_HEIGHT, Color.black.getRGB());

            Gui.drawRect(x + 1, y + 1, x + 5 + mc.fontRendererObj.getStringWidth(text), y + 2, new Color(51, 153, 255).getRGB());
            Gui.drawRect(x + 1, y + 2, x + 5 + mc.fontRendererObj.getStringWidth(text), y + 5.5f + mc.fontRendererObj.FONT_HEIGHT, new Color(0, 0, 0, 140).getRGB());

            mc.fontRendererObj.drawStringOutlined(text, 13, 13, new Color(51, 153, 255).getRGB());
        }
    }

}
