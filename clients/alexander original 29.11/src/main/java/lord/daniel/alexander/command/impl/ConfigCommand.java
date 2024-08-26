package lord.daniel.alexander.command.impl;

import lord.daniel.alexander.command.abstracts.Command;
import lord.daniel.alexander.command.data.CommandData;
import lord.daniel.alexander.config.Config;
import lord.daniel.alexander.storage.impl.ConfigStorage;

@CommandData(name = "config", aliases = {"config", "c", "conf"}, usage = "[load/save/delete/list/reload] [Config]")
public class ConfigCommand extends Command {

    @Override
    public boolean execute(String[] args) {
        if (args.length >= 1) {
            String upperCaseFunction = args[0].toUpperCase();

            if (args.length == 2) {
                switch (upperCaseFunction) {
                    case "LOAD":
                        if (ConfigStorage.getConfigStorage().loadConfig(args[1]))
                            sendMessage("Successfully loaded config: '" + args[1] + "'");
                        else
                            sendMessage("Failed to load config: '" + args[1] + "'");
                        break;
                    case "SAVE":
                        if (ConfigStorage.getConfigStorage().saveConfig(args[1]))
                            sendMessage("Successfully saved config: '" + args[1] + "'");
                        else
                            sendMessage("Failed to save config: '" + args[1] + "'");
                        break;
                    case "DELETE":
                        if (ConfigStorage.getConfigStorage().deleteConfig(args[1]))
                            sendMessage("Successfully deleted config: '" + args[1] + "'");
                        else
                            sendMessage("Failed to delete config: '" + args[1] + "'");
                        break;
                }
                return true;
            } else if (args.length == 1) {
                switch (upperCaseFunction) {
                    case "LIST" -> {
                        sendMessage("Available Configs:");
                        for (Config config : ConfigStorage.getConfigStorage().getList())
                            sendMessage(config.getName());       
                    }
                    case "RELOAD" -> {
                        ConfigStorage.getConfigStorage().reloadConfigs();
                    }
                }
                return true;
            }
        }
        return false;
    }
}