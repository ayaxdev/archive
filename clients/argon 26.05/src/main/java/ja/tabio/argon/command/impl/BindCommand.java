package ja.tabio.argon.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ja.tabio.argon.command.Command;
import ja.tabio.argon.command.annotation.RegisterCommand;
import ja.tabio.argon.command.argument.ModuleArgumentType;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.utils.chat.ChatUtils;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@RegisterCommand
public class BindCommand extends Command {
    public BindCommand() {
        super("bind", new String[]{"b"});
    }

    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(arg("module", new ModuleArgumentType())
                .then(arg("key", StringArgumentType.word()).executes(context -> {
                    final Module module = context.getArgument("module", Module.class);
                    final String stringKey = context.getArgument("key", String.class);

                    if (stringKey == null) {
                        ChatUtils.sendMessage(module.getName() + " is bound to " + Formatting.GRAY + module.key);
                        return SINGLE_SUCCESS;
                    }

                    int key;

                    if (stringKey.equalsIgnoreCase("none") || stringKey.equalsIgnoreCase("null")) {
                        key = -1;
                    } else {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + stringKey.toLowerCase()).getCode();
                        } catch (NumberFormatException e) {
                            ChatUtils.sendMessage("There is no such button");
                            return SINGLE_SUCCESS;
                        }
                    }

                    if (key == 0) {
                        ChatUtils.sendMessage("Unknown key '" + stringKey + "'!");
                        return SINGLE_SUCCESS;
                    }

                    module.key = key;

                    ChatUtils.sendMessage("Bound " + Formatting.GREEN + module.getName() + Formatting.WHITE + " to " + Formatting.GRAY + stringKey.toUpperCase());

                    return SINGLE_SUCCESS;
                }))
        );
    }
}
