package lord.daniel.alexander.command.impl;

import lord.daniel.alexander.command.abstracts.Command;
import lord.daniel.alexander.command.data.CommandData;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.impl.number.KeyBindValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import org.lwjgl.input.Keyboard;
@CommandData(name = "bind", aliases = {"bind", "b"}, usage = "[Module] [Key]")
public class BindCommand extends Command {

    @Override
    public boolean execute(String[] args) {
        if(args.length == 2) {
            final AbstractModule module = ModuleStorage.getModuleStorage().getByName(args[0]);
            if(module != null) {
                final int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                ((KeyBindValue)module.getSettingByName("BindKey")).setValue(key);
                sendMessage("Key set to " + args[1].toUpperCase());
            }else{
                sendMessage("Module " + args[0] + " not found!");
            }
        } else {
            return false;
        }
        return true;
    }
}