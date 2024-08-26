package lord.daniel.alexander.storage.impl;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.command.abstracts.Command;
import lord.daniel.alexander.command.data.CommandData;
import lord.daniel.alexander.event.impl.game.SendChatMessageEvent;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.storage.Storage;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class CommandStorage extends Storage<Command> implements Methods {

    @Getter
    @Setter
    public static CommandStorage commandStorage;

    @Override
    public void init() {
        Modification.INSTANCE.getBus().subscribe(this);
        final Reflections reflections = new Reflections("lord.daniel.alexander");
        reflections.getTypesAnnotatedWith(CommandData.class).forEach(aClass -> {
            try {
                add((Command) aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        getList().sort(Comparator.comparing(Command::getName));
    }

    @EventLink
    public final Listener<SendChatMessageEvent> sendChatMessageEventListener = sendChatMessageEvent -> {
        if (sendChatMessageEvent.getMessage().startsWith(Modification.COMMAND_PREFIX)) {
            sendChatMessageEvent.setCancelled(true);
            final String[] args = sendChatMessageEvent.getMessage().split(" ");
            final String cmd = args[0].substring(1);
            this.getList().forEach(command -> {
                if (command.getName().equalsIgnoreCase(cmd) || Arrays.stream(command.getAliases()).anyMatch(s -> s.equalsIgnoreCase(cmd)))
                    if (!command.execute(Arrays.copyOfRange(args, 1, args.length))) {
                        sendMessage("Wrong Usage, correct usage: " + Modification.COMMAND_PREFIX + cmd + " " + command.getUsage());
                    }
            });
            return;
        }
    };

}
