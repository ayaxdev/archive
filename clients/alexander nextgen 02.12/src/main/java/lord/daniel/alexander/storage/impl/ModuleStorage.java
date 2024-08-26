package lord.daniel.alexander.storage.impl;

import io.github.nevalackin.radbus.Listen;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.input.KeyInputEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.annotations.CreateModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.storage.Storage;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class ModuleStorage extends Storage<AbstractModule> {

    @Getter
    @Setter
    private static ModuleStorage moduleStorage;

    @Override
    public void init() {
        Modification.getModification().getPubSub().subscribe(this);
        final Reflections reflections = new Reflections("lord.daniel.alexander");
        reflections.getTypesAnnotatedWith(CreateModule.class).forEach(aClass -> {
            try {
                this.add((AbstractModule) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Listen
    public final void onKey(KeyInputEvent keyInputEvent) {
        getList().forEach(module -> {
            if(module.getKey() == keyInputEvent.getKey())
                module.toggle();
        });
    }

    public final List<AbstractModule> getByCategory(EnumModuleType enumModuleType) {
        return getList().stream().filter(module -> module.getCategory() == enumModuleType).collect(Collectors.toList());
    }

}
