package ja.tabio.argon.module.enums;

import de.florianmichael.rclasses.common.array.ArrayUtils;
import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.ICategory;
import ja.tabio.argon.module.Module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public enum ModuleCategory implements ICategory, Argon.IArgonAccess {
    HACK("Hack"),
    VISUAL("Visual");

    final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Object> getObjects() {
        final Object[] objects = switch (this) {
            case HACK -> HackCategory.values();
            case VISUAL -> VisualCategory.values();
        };
        return List.of(objects);
    }
}
