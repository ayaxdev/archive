package lord.daniel.alexander.event.impl.client;

import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.module.abstracts.AbstractModule;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class ModuleEnableEvent extends Event {
    private final AbstractModule module;

    public ModuleEnableEvent(Stage stage, AbstractModule module) {
        super(stage);
        this.module = module;
    }

}
