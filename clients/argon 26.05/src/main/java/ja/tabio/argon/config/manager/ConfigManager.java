package ja.tabio.argon.config.manager;

import com.google.gson.Gson;
import ja.tabio.argon.Argon;
import ja.tabio.argon.config.Config;
import ja.tabio.argon.config.annotation.RegisterConfig;
import ja.tabio.argon.interfaces.Initializable;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager implements Initializable {

    public final Gson gson = new Gson();

    public final Map<Class<?>, Config> configMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init(final String[] args) {
        final Reflections reflections = new Reflections("ja.tabio.argon.config.impl");
        this.classes = reflections.getTypesAnnotatedWith(RegisterConfig.class);

        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                for (Constructor<?> constructor : clazz.getConstructors()) {
                    if (constructor.getParameterCount() == 0) {
                        final Object object = constructor.newInstance();

                        if (object instanceof Config config) {
                            configMap.put(clazz, config);
                            config.read();
                        }

                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a config instance", e);
            }
        }

        Argon.getInstance().logger.info("Registered {} configs", configMap.size());
    }

    public void save() {
        for (Config config : configMap.values())
            config.save();
    }
}
