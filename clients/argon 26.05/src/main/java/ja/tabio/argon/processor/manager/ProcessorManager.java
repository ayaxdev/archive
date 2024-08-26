package ja.tabio.argon.processor.manager;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Initializable;
import ja.tabio.argon.interfaces.Minecraft;
import ja.tabio.argon.processor.Processor;
import ja.tabio.argon.processor.annotation.RegisterProcessor;
import org.reflections.Reflections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ProcessorManager implements Initializable, Minecraft {

    public final Map<Class<?>, Processor> processorMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init(final String[] args) {
        final Reflections reflections = new Reflections("ja.tabio.argon.processor.impl");
        this.classes = reflections.getTypesAnnotatedWith(RegisterProcessor.class);

        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                final Object object = clazz.getDeclaredConstructor().newInstance();

                if (object instanceof Processor processor) {
                    processor.initialize();
                    processorMap.put(clazz, processor);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a processor instance", e);
            }
        }

        Argon.getInstance().logger.info("Registered {} processor", processorMap.size());
    }

    @SuppressWarnings("unchecked")
    public <T extends Processor> T getByClass(Class<T> clazz) {
        return (T) processorMap.get(clazz);
    }

}

