package lord.daniel.alexander.event.impl.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.module.abstracts.AbstractModule;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Getter
@RequiredArgsConstructor
public class EnableModuleEvent extends Event {

    private final AbstractModule abstractModule;
    final Type type;

    public enum Type {
        PRE, POST;
    }

}
