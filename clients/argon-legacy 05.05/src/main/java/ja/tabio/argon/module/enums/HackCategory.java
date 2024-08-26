package ja.tabio.argon.module.enums;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.ICategory;

import java.util.List;
import java.util.stream.Collectors;

public enum HackCategory implements ICategory, Argon.IArgonAccess {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    EXPLOIT("Exploit"),
    RENDER("Render"),
    WORLD("World");

    final String name;

    HackCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Object> getObjects() {
        return getModuleManager().moduleMap.values().stream().filter(module -> module.moduleData.category() == ModuleCategory.HACK && module.hackData != null && module.hackData.hackCategory() == this).collect(Collectors.toList());
    }
}
