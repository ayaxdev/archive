package ja.tabio.argon.config.manager;

import ja.tabio.argon.Argon;
import ja.tabio.argon.config.Config;
import ja.tabio.argon.config.annotation.ConfigData;
import ja.tabio.argon.interfaces.IClientInitializeable;
import org.reflections.Reflections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager implements IClientInitializeable, Argon.IArgonAccess {

    public final Map<Class<Config>, Config> configMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init() {
        final Reflections reflections = new Reflections("ja.tabio.argon.config.impl");
        this.classes = reflections.getTypesAnnotatedWith(ConfigData.class);

        getBus().subscribe(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                final Config config = (Config) clazz.getDeclaredConstructor().newInstance();
                configMap.put((Class<Config>) clazz, config);
                config.read();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a module instance (is class a module?)", e);
            }
        }

        getLogger().info("Registered {} modules", configMap.size());
    }

    public void stop() {
        try {
            configMap.values().forEach(Config::write);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read configs");
        }
    }

}
