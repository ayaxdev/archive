package ja.tabio.argon.module.enums;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Category;
import ja.tabio.argon.module.Module;

import java.util.ArrayList;
import java.util.List;

public enum ModuleCategory implements Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    RENDER("Render");

    final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    @Override
    public List<Object> get() {
        if (!Argon.getInstance().loaded)
            return List.of();

        final List<Module> modules = Argon.getInstance().moduleManager.moduleMap
                .values().stream().filter(module -> module.moduleCategory == this).toList();
        return new ArrayList<>(modules);
    }

    public String getName() {
        return name;
    }

    public String getUniqueIdentifier() {
        return "ModuleCategory-" + getName();
    }
}
