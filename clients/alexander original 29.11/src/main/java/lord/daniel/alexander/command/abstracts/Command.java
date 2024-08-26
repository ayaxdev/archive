package lord.daniel.alexander.command.abstracts;

import lombok.Getter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.command.data.CommandData;
import lord.daniel.alexander.interfaces.Methods;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Getter
public abstract class Command implements Methods {

    final String name, usage;
    final String[] aliases;

    public Command() {
        final CommandData info = getClass().getAnnotation(CommandData.class);
        name = info.name();
        usage = info.usage();
        aliases = info.aliases().length != 0 ? info.aliases() : new String[] {name};
    }

    public abstract boolean execute(String[] args);

    public String getPrefix() {
        return Modification.COMMAND_PREFIX;
    }

}
