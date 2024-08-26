package net.jezevcik.argon.module.choice;

import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.module.extension.Extension;

public class Choice extends Extension {

    public Choice(String name, Module parent) {
        super(name, parent.config, false);
    }

    @Override
    public String toString() {
        return name;
    }

}
