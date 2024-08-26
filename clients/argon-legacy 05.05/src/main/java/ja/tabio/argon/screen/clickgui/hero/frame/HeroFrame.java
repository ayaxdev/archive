package ja.tabio.argon.screen.clickgui.hero.frame;

import de.florianmichael.rclasses.math.integration.Boundings;
import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.ICategory;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.interfaces.INameable;
import ja.tabio.argon.interfaces.IToggleable;
import ja.tabio.argon.screen.clickgui.hero.window.HeroWindow;
import ja.tabio.argon.setting.interfaces.ISettings;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.List;

public class HeroFrame implements IMinecraft, Argon.IArgonAccess {

    public final ICategory category;
    private final List<Object> objects;

    public HeroWindow heroWindow;

    private float dragX = Float.MIN_VALUE,
                dragY = Float.MIN_VALUE;

    public float x, y, width, height;
    public boolean expanded = false;

    public HeroFrame(ICategory category, float x, float y, float width, float height) {
        this.category = category;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.objects = category.getObjects();
    }

    public void draw(int mouseX, int mouseY) {
        if (dragX != Float.MIN_VALUE && dragY != Float.MIN_VALUE) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        Gui.drawRect(x + 2, y, x + width, y + height, -15592942);
        Gui.drawRect(x, y, x + 2, y + height, new Color(255, 26, 42, 170).getRGB());
        mc.fontRendererObj.drawString(category.getName(), x + 4, y + height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2f, -1052689);

        if (!expanded)
            return;

        final float moduleHeight = mc.fontRendererObj.FONT_HEIGHT + 3;
        float moduleY = y + height;

        for (Object o : objects) {
            if (!(o instanceof INameable nameable))
                continue;

            Gui.drawRect(x + 2, moduleY, x + width, moduleY + moduleHeight, 0xff232323);
            Gui.drawRect(x, moduleY, x + 2, moduleY + moduleHeight, new Color(255, 26, 42, 170).getRGB());

            if (Boundings.isInBounds(mouseX, mouseY, x, moduleY, width, moduleHeight))
                Gui.drawRect(x + 2, moduleY, x + width, moduleY + moduleHeight, 1427181841);

            mc.fontRendererObj.drawString(nameable.getDisplayName(), x + 2 + (width - 2) / 2f - mc.fontRendererObj.getStringWidth(nameable.getDisplayName()) / 2f,
                    moduleY + moduleHeight / 2 - mc.fontRendererObj.FONT_HEIGHT / 2f, o instanceof IToggleable toggleable && toggleable.isEnabled() ? -1052689 : -5263441);

            if (heroWindow != null && heroWindow.settingsObject == o) {
                heroWindow.x = x + width;
                heroWindow.y = moduleY;

                heroWindow.draw(mouseX, mouseY);
            }

            moduleY += moduleHeight;
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if (Boundings.isInBounds(mouseX, mouseY, x, y, width, height)) {
            if (button == 1) {
                expanded = !expanded;
            } else if (button == 0) {
                dragX = mouseX - x;
                dragY = mouseY - y;
            }

            return;
        }

        if (!expanded)
            return;

        final float moduleHeight = mc.fontRendererObj.FONT_HEIGHT + 3;
        float moduleY = y + height;

        for (Object o : objects) {
            if (!(o instanceof INameable nameable) || !(o instanceof ISettings objectWithSettings))
                continue;

            if (Boundings.isInBounds(mouseX, mouseY, x, moduleY, width, moduleHeight)) {
                if (button == 0 && o instanceof IToggleable toggleable) {
                    toggleable.changeEnabled();
                } else if (button == 1) {
                    if (heroWindow != null && heroWindow.settingsObject.equals(objectWithSettings))
                        heroWindow = null;
                    else
                        heroWindow = new HeroWindow(objectWithSettings, x + width, moduleY);
                }
            }

            moduleY += moduleHeight;
        }

        if (heroWindow != null)
            heroWindow.mouseClick(mouseX, mouseY, button);
    }

    public void mouseRelease(int mouseX, int mouseY, int button) {
        dragX = Float.MIN_VALUE;
        dragY = Float.MIN_VALUE;
    }
}
