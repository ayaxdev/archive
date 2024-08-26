package ja.tabio.argon.module.enums;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.ICategory;

import java.util.List;
import java.util.stream.Collectors;

public enum VisualCategory implements ICategory, Argon.IArgonAccess {
    GUI("GUI"),
    HUD("HUD");

    final String name;

    VisualCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Object> getObjects() {
        return getModuleManager().moduleMap.values().stream().filter(module -> module.moduleData.category() == ModuleCategory.VISUAL && module.visualData != null && module.visualData.visualCategory() == this).collect(Collectors.toList());
    }
}
