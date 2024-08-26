package ja.tabio.argon.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ja.tabio.argon.Argon;
import ja.tabio.argon.module.Module;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ModuleArgumentType implements ArgumentType<Module> {
    private static final Collection<String> EXAMPLES = Argon.getInstance().moduleManager.moduleMap.values().stream().map(Module::getName).limit(5).toList();

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        try {
            return Argon.getInstance().moduleManager.getByName(reader.readString());
        } catch (Exception e) {
            throw new DynamicCommandExceptionType(
                    name -> Text.literal(String.format("Module %s not found", e))
            ).create(reader.readString());
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Argon.getInstance().moduleManager.moduleMap.values().stream().map(Module::getName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
