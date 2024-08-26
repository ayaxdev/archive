package ja.tabio.argon.items;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Initializable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.items.annotation.RegisterItemGroup;
import org.reflections.Reflections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ItemGroupManager implements Initializable, Minecraft {

    public final Map<Class<?>, ClientItemGroup> itemGroupMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init(final String[] args) {
        final Reflections reflections = new Reflections("ja.tabio.argon.items.impl");
        this.classes = reflections.getTypesAnnotatedWith(RegisterItemGroup.class);

        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                final Object object = clazz.getDeclaredConstructor().newInstance();

                if (object instanceof ClientItemGroup clientItemGroup) {
                    clientItemGroup.init();
                    itemGroupMap.put(clazz, clientItemGroup);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a module instance", e);
            }
        }

        Argon.getInstance().logger.info("Registered {} modules", itemGroupMap.size());
    }

}
