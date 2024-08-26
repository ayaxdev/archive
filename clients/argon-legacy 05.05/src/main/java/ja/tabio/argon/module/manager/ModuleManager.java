package ja.tabio.argon.module.manager;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.HandleKeyEvent;
import ja.tabio.argon.interfaces.IClientInitializeable;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.ModuleData;
import org.reflections.Reflections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ModuleManager implements IClientInitializeable, IMinecraft, Argon.IArgonAccess {

    public final Map<Class<Module>, Module> moduleMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init() {
        final Reflections reflections = new Reflections("ja.tabio.argon.module.impl");
        this.classes = reflections.getTypesAnnotatedWith(ModuleData.class);

        getBus().subscribe(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                final Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                module.postInit();
                moduleMap.put((Class<Module>) clazz, module);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a module instance (is class a module?)", e);
            }
        }

        getLogger().info("Registered {} modules", moduleMap.size());
    }

    @EventHandler
    public final void onKey(HandleKeyEvent handleKeyEvent) {
        for (Module module : moduleMap.values()) {
            if (handleKeyEvent.key == module.key)
                module.changeEnabled();
        }
    }

}
