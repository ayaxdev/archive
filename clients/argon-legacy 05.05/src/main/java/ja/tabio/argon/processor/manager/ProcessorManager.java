package ja.tabio.argon.processor.manager;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.IClientInitializeable;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.processor.annotation.ProcessorData;
import org.reflections.Reflections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ProcessorManager implements IClientInitializeable, IMinecraft, Argon.IArgonAccess {

    private final Map<Class<Processor>, Processor> moduleMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init() {
        final Reflections reflections = new Reflections("ja.tabio.argon.processor.impl");
        this.classes = reflections.getTypesAnnotatedWith(ProcessorData.class);

        getBus().subscribe(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                final Processor processor = (Processor) clazz.getDeclaredConstructor().newInstance();
                processor.init();
                moduleMap.put((Class<Processor>) clazz, processor);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a processor instance (is class a processor?)", e);
            }
        }

        getLogger().info("Registered {} processors", moduleMap.size());
    }

}
