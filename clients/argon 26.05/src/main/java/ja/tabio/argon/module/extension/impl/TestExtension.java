package ja.tabio.argon.module.extension.impl;

import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.extension.Extension;
import ja.tabio.argon.setting.impl.BooleanSetting;

public class TestExtension extends Extension {

    public final BooleanSetting tst = new BooleanSetting("Test", true);

    public TestExtension(Module parent) {
        super("Test", parent, true);
    }

}
