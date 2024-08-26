package ja.tabio.argon.module.manager;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.HandleKeyEvent;
import ja.tabio.argon.interfaces.Initializable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ModuleManager implements Initializable, Minecraft {

    public final Map<Class<?>, Module> moduleMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init(final String[] args) {
        final Reflections reflections = new Reflections("ja.tabio.argon.module");
        this.classes = reflections.getTypesAnnotatedWith(RegisterModule.class);

        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                for (Constructor<?> constructor : clazz.getConstructors()) {
                    if (constructor.getParameterCount() == 0) {
                        final Object object = constructor.newInstance();

                        if (object instanceof Module module) {
                            module.postInit();
                            moduleMap.put(clazz, module);
                        }

                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a module instance", e);
            }
        }

        Argon.getInstance().logger.info("Registered {} modules", moduleMap.size());
    }

    @EventHandler
    public final void onKey(HandleKeyEvent handleKeyEvent) {
        if (mc.currentScreen != null || handleKeyEvent.action != HandleKeyEvent.KeyAction.PRESS)
            return;

        for (Module module : moduleMap.values()) {
            if (handleKeyEvent.key == module.key)
                module.changeEnabled();
        }
    }

    public Module getByName(String name) {
        return this.moduleMap.values().stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElseThrow();
    }

}
