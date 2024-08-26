package lord.daniel.alexander.command.impl;

import lord.daniel.alexander.command.abstracts.Command;
import lord.daniel.alexander.command.data.CommandData;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;

@CommandData(name = "toggle", aliases = {"toggle", "t"}, usage = "[Module]")
public class ToggleCommand extends Command {

    @Override
    public boolean execute(String[] args) {
        if(args.length == 1) {
            final AbstractModule module = ModuleStorage.getModuleStorage().getByName(args[0]);
            if(module != null) {
                module.toggle();
                sendMessage("Toggled " + args[0]);
            }else{
                sendMessage("Module " + args[0] + " not found!");
            }
        } else {
            return false;
        }
        return true;
    }
}