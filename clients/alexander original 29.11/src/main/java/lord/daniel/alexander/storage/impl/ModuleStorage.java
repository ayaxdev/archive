package lord.daniel.alexander.storage.impl;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.impl.game.KeyPressEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.storage.Storage;
import lord.daniel.alexander.util.java.ArrayUtils;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class ModuleStorage extends Storage<AbstractModule> {

    @Getter
    @Setter
    private static ModuleStorage moduleStorage;

    @Override
    public void init() {
        Modification.INSTANCE.getBus().subscribe(this);
        final Reflections reflections = new Reflections("lord.daniel.alexander");
        reflections.getTypesAnnotatedWith(ModuleData.class).forEach(aClass -> {
            try {
                this.add((AbstractModule) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @EventLink
    public final Listener<KeyPressEvent> keyPressEventListener = keyPressEvent -> {
        for(AbstractModule abstractModule : getList()) {
            if(abstractModule.getKey() == keyPressEvent.getKey()) {
                abstractModule.toggle();
            }
        }
    };

    public List<AbstractModule> getModules(EnumModuleType type) {
        return this.getList().stream().filter(mod -> ArrayUtils.contains(mod.getCategories(), type)).collect(Collectors.toList());
    }

    public <V extends AbstractModule> V getByName(final String name) {
        final AbstractModule feature = this.getList().stream().filter(T -> T.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (feature == null) return null;
        return (V) feature;
    }

}
