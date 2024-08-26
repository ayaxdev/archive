package ja.tabio.argon.command.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.Argon;
import ja.tabio.argon.command.Command;
import ja.tabio.argon.command.annotation.RegisterCommand;
import ja.tabio.argon.event.impl.ChatEvent;
import ja.tabio.argon.interfaces.Initializable;
import ja.tabio.argon.interfaces.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.SentMessage;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CommandManager implements Initializable, Minecraft {

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final CommandSource source = new ClientCommandSource(null, MinecraftClient.getInstance());

    public final Map<Class<?>, Command> commandMap = new LinkedHashMap<>();

    private Set<Class<?>> classes;

    @Override
    public void init(final String[] args) {
        final Reflections reflections = new Reflections("ja.tabio.argon.command");
        this.classes = reflections.getTypesAnnotatedWith(RegisterCommand.class);

        Argon.getInstance().eventBus.subscribe(this);
    }

    @Override
    public void start() {
        for (Class<?> clazz : classes) {
            try {
                for (Constructor<?> constructor : clazz.getConstructors()) {
                    if (constructor.getParameterCount() == 0) {
                        final Object object = constructor.newInstance();

                        if (object instanceof Command command) {
                            command.register(dispatcher);
                            commandMap.put(clazz, command);
                        }

                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a command instance", e);
            }
        }

        Argon.getInstance().logger.info("Registered {} commands", commandMap.size());
    }

    @EventHandler
    public final void onChat(ChatEvent chatEvent) {
        if (chatEvent.message.startsWith(".")) {
            try {
                dispatcher.execute(chatEvent.message.substring(1), source);
            } catch (CommandSyntaxException ignored) {}

            chatEvent.cancelled = true;
        }
    }


}
