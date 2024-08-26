package ja.tabio.argon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Nameable;
import net.minecraft.command.CommandSource;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public abstract class Command implements Nameable, Identifiable {

    public final String name;
    public final String[] aliases, possible;

    public Command(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;

        this.possible = ArrayUtils.add(aliases, name);
    }

    public abstract void executeBuild(LiteralArgumentBuilder<CommandSource> builder);


    protected static <T> @NotNull RequiredArgumentBuilder<CommandSource, T> arg(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static @NotNull LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String name : possible) {
            LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
            executeBuild(builder);
            dispatcher.register(builder);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUniqueIdentifier() {
        return String.format("Command-%s", getName());
    }
}
