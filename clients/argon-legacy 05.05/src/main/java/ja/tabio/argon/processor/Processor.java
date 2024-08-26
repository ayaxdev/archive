package ja.tabio.argon.processor;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.interfaces.INameable;
import ja.tabio.argon.processor.annotation.ProcessorData;

public class Processor implements IMinecraft, INameable {

    public final String name;

    public Processor() {
        final ProcessorData annotation = getClass().getAnnotation(ProcessorData.class);

        if (annotation == null)
            throw new IllegalStateException(String.format("Processor %s is not annotated with @ProcessorData", getClass().getSimpleName()));

        this.name = annotation.name();
    }

    public void init() {
        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public String getName() {
        return name;
    }
}
