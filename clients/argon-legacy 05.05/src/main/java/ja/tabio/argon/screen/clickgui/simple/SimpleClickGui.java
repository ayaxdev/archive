package ja.tabio.argon.screen.clickgui.simple;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.Argon;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.screen.clickgui.ClickGui;
import net.minecraft.client.gui.GuiButton;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SimpleClickGui extends ClickGui {

    private final List<Module> modules;

    public SimpleClickGui() {
        this(new LinkedList<>(Argon.getInstance().moduleManager.moduleMap.values()));
    }

    public SimpleClickGui(List<Module> modules) {
        this.modules = modules;
    }

    public void initGui() {
        reset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        int index = 0;

        for (Module module : modules) {
            if (index == guiButton.id)
                mc.displayGuiScreen(new ModuleScreen(module, this));

            index++;
        }
    }

    @Override
    public void reset() {
        buttonList.clear();

        for (int i = 0; i < modules.size(); i++) {
            final Module module = modules.get(i);

            buttonList.add(new GuiButton(i, 5, 5 + i * 25, module.getDisplayName()));
        }
    }

    @Override
    public JSONObject serialize() { return null; }

    @Override
    public void deserialize(JSONObject jsonObject) { }
}
