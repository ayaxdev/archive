package ja.tabio.argon.module.primitive;

import ja.tabio.argon.module.Module;

public abstract class PrimitiveModule extends Module {

    public PrimitiveModule(ModuleParams moduleParams) {
        super(moduleParams);
    }

    @Override
    protected final boolean onEnable() {
        run();

        return false;
    }

    protected abstract void run();

}
