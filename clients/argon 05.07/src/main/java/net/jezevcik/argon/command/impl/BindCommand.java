package net.jezevcik.argon.command.impl;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.command.Command;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.utils.chat.ChatUtils;
import net.jezevcik.argon.utils.keyboard.KeyboardUtils;
import org.lwjgl.glfw.GLFW;

public class BindCommand extends Command {

    public BindCommand() {
        super("Bind", new String[]{"bind", "b"}, Syntax.of(
                new Syntax.Element(Syntax.ElementType.REQUIRED, Syntax.InputType.MODULE, "module"),
                new Syntax.Element(Syntax.ElementType.REQUIRED, Syntax.InputType.KEY, "key")
        ), "Binds the provided module to the provided kez");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length != 3) {
            pushSyntaxError();

            return;
        }

        final Module module = ParekClient.getInstance().modules.getByName(args[1]);

        if (module == null) {
            pushSyntaxError();

            return;
        }

        try {
            module.key = KeyboardUtils.getKeyIndex(args[2]);

            ChatUtils.pushWithPrefix(String.format("Module %s has been bound to %s!", module.getIdentifier(IdentifierType.DISPLAY), GLFW.glfwGetKeyName(module.key, 0)), ChatUtils.Prefix.SUCCESS);
        } catch (Exception e) {
            pushSyntaxError();
        }
    }

}
