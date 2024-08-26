package ja.tabio.argon.screen.clickgui.hero;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.interfaces.ICategory;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.screen.clickgui.ClickGui;
import ja.tabio.argon.screen.clickgui.hero.frame.HeroFrame;
import net.minecraft.client.gui.Gui;

import java.util.LinkedList;
import java.util.List;

public class HeroClickGui extends ClickGui {

    private final List<HeroFrame> frames = new LinkedList<>();

    public HeroClickGui() {
        reset();
    }

    @Override
    public void initGui() {
        if (frames.isEmpty())
            reset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, -2012213232);

        for (HeroFrame heroFrame : frames) {
            heroFrame.draw(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        for (HeroFrame heroFrame : frames) {
            heroFrame.mouseClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        for (HeroFrame heroFrame : frames) {
            heroFrame.mouseRelease(mouseX, mouseY, button);
        }
    }

    @Override
    public void reset() {
        frames.clear();

        final float width = 82, height = 15, margin = 10;
        float x = 20, y = 20;

        for (ModuleCategory moduleCategory : ModuleCategory.values()) {
            for (Object object : moduleCategory.getObjects()) {
                if (!(object instanceof ICategory category))
                    continue;

                frames.add(new HeroFrame(category, x, y, width, height));
                y += height + margin;
            }
        }
    }

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();

        for (HeroFrame frame : frames) {
            final JSONObject frameObject = new JSONObject();

            frameObject.put("enabled", frame.expanded);
            frameObject.put("x", frame.x);
            frameObject.put("y", frame.y);

            jsonObject.put(frame.category.getName(), frameObject);
        }

        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        for (HeroFrame frame : frames) {
            if (jsonObject.containsKey(frame.category.getName())) {
                final JSONObject frameObject = jsonObject.getJSONObject(frame.category.getName());

                frame.expanded = frameObject.getBooleanValue("enabled");
                frame.x = frameObject.getFloatValue("x");
                frame.y = frameObject.getFloatValue("y");
            }
        }
    }
}
