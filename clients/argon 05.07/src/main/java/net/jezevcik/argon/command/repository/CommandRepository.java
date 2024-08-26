package net.jezevcik.argon.command.repository;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.command.Command;
import net.jezevcik.argon.event.impl.ChatEvent;
import net.jezevcik.argon.repository.ElementRepository;
import net.jezevcik.argon.repository.params.RepositoryParams;
import net.jezevcik.argon.system.initialize.InitializeStage;
import net.jezevcik.argon.utils.chat.ChatUtils;

public class CommandRepository extends ElementRepository<Command> {

    public CommandRepository() {
        super("Commands", RepositoryParams.<Command>builder()
                .reflectClasses(true)
                .parentType(Command.class)
                .build());
    }

    @Override
    public final void init(InitializeStage stage) {
        super.init(stage);

        if (stage == InitializeStage.POST_MINECRAFT)
            ParekClient.getInstance().eventBus.subscribe(this);
    }

    @EventHandler
    public final void onChat(final ChatEvent chatEvent) {
        if (!chatEvent.message.startsWith("."))
            return;

        final String[] split = chatEvent.message.split(" ");

        if (split.length < 1)
            return;

        final String name = split[0].substring(1);

        chatEvent.cancelled = true;

        for (final Command command : this) {
            for (final String alias : command.aliases) {
                if (alias.equals(name)) {
                    command.execute(split);

                    return;
                }
            }
        }

        ChatUtils.pushWithPrefix("No such command exists!", ChatUtils.Prefix.ERROR);
    }

}
