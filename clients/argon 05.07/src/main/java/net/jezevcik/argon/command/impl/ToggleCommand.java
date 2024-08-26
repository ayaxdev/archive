package net.jezevcik.argon.command.impl;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.command.Command;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.utils.chat.ChatUtils;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle", new String[]{"toggle", "t"}
                , Syntax.of(new Syntax.Element(Syntax.ElementType.REQUIRED, Syntax.InputType.MODULE, "module"))
                , "Toggles the provided module");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 2) {
            pushSyntaxError();

            return;
        }

        final Module module = ParekClient.getInstance().modules.getByName(args[1]);

        if (module == null) {
            pushSyntaxError();

            return;
        }

        module.toggle();

        ChatUtils.pushWithPrefix(String.format("Module %s has been %s!", module.getIdentifier(IdentifierType.DISPLAY), module.isEnabled() ? "enabled" : "disabled"), ChatUtils.Prefix.SUCCESS);
    }

}
